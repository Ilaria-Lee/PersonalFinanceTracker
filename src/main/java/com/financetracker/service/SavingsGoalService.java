// FILE: src/main/java/com/financetracker/service/SavingsGoalService.java
package com.financetracker.service;

import com.financetracker.dto.SavingsContributionDto;
import com.financetracker.dto.SavingsGoalDto;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.exception.UnauthorizedAccessException;
import com.financetracker.exception.ValidationException;
import com.financetracker.model.SavingsContribution;
import com.financetracker.model.SavingsGoal;
import com.financetracker.model.User;
import com.financetracker.repository.SavingsContributionRepository;
import com.financetracker.repository.SavingsGoalRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SavingsGoalService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final SavingsGoalRepository savingsGoalRepository;
    private final SavingsContributionRepository savingsContributionRepository;

    public List<SavingsGoal> getGoalsForUser(User user) {
        return savingsGoalRepository.findByUser(user);
    }

    public SavingsGoal createGoal(SavingsGoalDto dto, User user) {
        validateGoalData(dto.getTargetAmount(), dto.getDeadline());

        SavingsGoal goal = SavingsGoal.builder()
                .name(dto.getName())
                .targetAmount(dto.getTargetAmount())
                .deadline(dto.getDeadline())
                .user(user)
                .build();

        @SuppressWarnings("null")
        SavingsGoal savedGoal = savingsGoalRepository.save(goal);
        log.info("Savings goal created: {} for user {}", savedGoal.getId(), user.getEmail());
        return savedGoal;
    }

    public SavingsGoal updateGoal(Long id, SavingsGoalDto dto, User user) {
        SavingsGoal goal = findByIdAndUser(id, user);
        validateGoalData(dto.getTargetAmount(), dto.getDeadline());

        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setDeadline(dto.getDeadline());

        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);
        log.info("Savings goal updated: {} for user {}", updatedGoal.getId(), user.getEmail());
        return updatedGoal;
    }

    @SuppressWarnings("null")
    public void deleteGoal(Long id, User user) {
        SavingsGoal goal = findByIdAndUser(id, user);
        List<SavingsContribution> contributions = savingsContributionRepository.findByGoal(goal);
        if (!contributions.isEmpty()) {
            savingsContributionRepository.deleteAll(contributions);
        }
        savingsGoalRepository.delete(goal);
        log.info("Savings goal deleted: {} for user {}", id, user.getEmail());
    }

    @SuppressWarnings("null")
    public void addContribution(Long goalId, SavingsContributionDto dto, User user) {
        SavingsGoal goal = findByIdAndUser(goalId, user);

        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Contribution amount must be greater than 0");
        }

        SavingsContribution contribution = SavingsContribution.builder()
                .amount(dto.getAmount())
                .date(dto.getDate())
                .goal(goal)
                .user(user)
                .build();

        savingsContributionRepository.save(contribution);
        log.info("Contribution added to goal {} for user {}", goalId, user.getEmail());
    }

    public BigDecimal getTotalContributions(SavingsGoal goal) {
        BigDecimal total = savingsContributionRepository.sumAmountByGoal(goal);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getProgressPercentage(SavingsGoal goal) {
        if (goal.getTargetAmount() == null || goal.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
        }

        BigDecimal percentage = getTotalContributions(goal)
                .multiply(HUNDRED)
                .divide(goal.getTargetAmount(), 1, RoundingMode.HALF_UP);

        return percentage.min(HUNDRED);
    }

    private SavingsGoal findByIdAndUser(Long id, User user) {
        @SuppressWarnings("null")
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found"));

        Long goalUserId = goal.getUser().getId();
        
        if (!Objects.equals(goalUserId, user.getId())) {
            throw new UnauthorizedAccessException("Unauthorized access to savings goal");
        }

        return goal;
    }

    private void validateGoalData(BigDecimal targetAmount, java.time.LocalDate deadline) {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Target amount must be greater than 0");
        }

        if (deadline == null) {
            throw new ValidationException("Deadline is required");
        }
    }
}
