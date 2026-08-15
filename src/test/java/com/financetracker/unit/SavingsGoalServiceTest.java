package com.financetracker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.financetracker.dto.SavingsContributionDto;
import com.financetracker.dto.SavingsGoalDto;
import com.financetracker.exception.UnauthorizedAccessException;
import com.financetracker.exception.ValidationException;
import com.financetracker.model.SavingsContribution;
import com.financetracker.model.SavingsGoal;
import com.financetracker.model.User;
import com.financetracker.repository.SavingsContributionRepository;
import com.financetracker.repository.SavingsGoalRepository;
import com.financetracker.service.SavingsGoalService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SavingsGoalServiceTest {

    @Mock
    private SavingsGoalRepository savingsGoalRepository;

    @Mock
    private SavingsContributionRepository savingsContributionRepository;

    @InjectMocks
    private SavingsGoalService savingsGoalService;

    @Test
    void createGoal_success() {
        User user = User.builder().id(1L).email("user@example.com").build();
        SavingsGoalDto dto = new SavingsGoalDto(
                null,
                "Emergency fund",
                new BigDecimal("3000.00"),
                LocalDate.of(2099, 12, 31)
        );
        when(savingsGoalRepository.save(any(SavingsGoal.class)))
                .thenAnswer(invocation -> {
                    SavingsGoal goal = invocation.getArgument(0);
                    goal.setId(10L);
                    return goal;
                });

        SavingsGoal result = savingsGoalService.createGoal(dto, user);

        assertEquals(10L, result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getTargetAmount(), result.getTargetAmount());
        assertEquals(user, result.getUser());
    }

    @Test
    void createGoal_rejectsNonPositiveTarget() {
        User user = User.builder().id(1L).build();
        SavingsGoalDto dto = new SavingsGoalDto(null, "Invalid", BigDecimal.ZERO, LocalDate.of(2099, 12, 31));

        assertThrows(ValidationException.class, () -> savingsGoalService.createGoal(dto, user));
    }

    @Test
    void updateGoal_rejectsDifferentOwner() {
        User currentUser = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        SavingsGoal goal = SavingsGoal.builder().id(10L).user(owner).build();
        SavingsGoalDto dto = new SavingsGoalDto(
                10L,
                "Updated",
                new BigDecimal("2000.00"),
                LocalDate.of(2099, 12, 31)
        );
        when(savingsGoalRepository.findById(10L)).thenReturn(Optional.of(goal));

        assertThrows(UnauthorizedAccessException.class, () -> savingsGoalService.updateGoal(10L, dto, currentUser));
    }

    @Test
    void addContribution_success() {
        User user = User.builder().id(1L).email("user@example.com").build();
        SavingsGoal goal = SavingsGoal.builder().id(10L).user(user).build();
        SavingsContributionDto dto = new SavingsContributionDto(
                new BigDecimal("150.00"),
                LocalDate.of(2026, 8, 15)
        );
        when(savingsGoalRepository.findById(10L)).thenReturn(Optional.of(goal));

        savingsGoalService.addContribution(10L, dto, user);

        ArgumentCaptor<SavingsContribution> contributionCaptor =
                ArgumentCaptor.forClass(SavingsContribution.class);
        verify(savingsContributionRepository).save(contributionCaptor.capture());
        assertEquals(dto.getAmount(), contributionCaptor.getValue().getAmount());
        assertEquals(goal, contributionCaptor.getValue().getGoal());
        assertEquals(user, contributionCaptor.getValue().getUser());
    }

    @Test
    void deleteGoal_removesContributionsBeforeGoal() {
        User user = User.builder().id(1L).email("user@example.com").build();
        SavingsGoal goal = SavingsGoal.builder().id(10L).user(user).build();
        SavingsContribution contribution = SavingsContribution.builder().id(20L).goal(goal).user(user).build();
        when(savingsGoalRepository.findById(10L)).thenReturn(Optional.of(goal));
        when(savingsContributionRepository.findByGoal(goal)).thenReturn(List.of(contribution));

        savingsGoalService.deleteGoal(10L, user);

        InOrder deletionOrder = inOrder(savingsContributionRepository, savingsGoalRepository);
        deletionOrder.verify(savingsContributionRepository).deleteAll(List.of(contribution));
        deletionOrder.verify(savingsGoalRepository).delete(goal);
    }

    @Test
    void getProgressPercentage_capsResultAtOneHundred() {
        SavingsGoal goal = SavingsGoal.builder()
                .id(10L)
                .targetAmount(new BigDecimal("100.00"))
                .build();
        when(savingsContributionRepository.sumAmountByGoal(goal)).thenReturn(new BigDecimal("125.00"));

        BigDecimal result = savingsGoalService.getProgressPercentage(goal);

        assertEquals(0, new BigDecimal("100").compareTo(result));
    }
}
