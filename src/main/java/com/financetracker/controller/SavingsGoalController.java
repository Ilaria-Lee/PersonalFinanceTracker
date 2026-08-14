// FILE: src/main/java/com/financetracker/controller/SavingsGoalController.java
package com.financetracker.controller;

import com.financetracker.dto.SavingsContributionDto;
import com.financetracker.dto.SavingsGoalDto;
import com.financetracker.model.SavingsContribution;
import com.financetracker.model.SavingsGoal;
import com.financetracker.model.User;
import com.financetracker.repository.SavingsContributionRepository;
import com.financetracker.service.AuthService;
import com.financetracker.service.SavingsGoalService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/savings")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;
    private final SavingsContributionRepository savingsContributionRepository;
    private final AuthService authService;

    @GetMapping
    public String listSavings(Model model) {
        User user = authService.getCurrentUser();
        List<SavingsGoal> goals = savingsGoalService.getGoalsForUser(user);
        List<GoalWithProgress> goalList = new ArrayList<>();

        for (SavingsGoal goal : goals) {
            BigDecimal totalContributions = savingsGoalService.getTotalContributions(goal);
            BigDecimal progressPercentage = savingsGoalService.getProgressPercentage(goal);
            List<SavingsContribution> contributions = savingsContributionRepository.findByGoal(goal);
            goalList.add(new GoalWithProgress(goal, totalContributions, progressPercentage, contributions));
        }

        model.addAttribute("goalList", goalList);
        model.addAttribute("savingsGoalDto", new SavingsGoalDto());
        model.addAttribute("savingsContributionDto", new SavingsContributionDto());
        return "savings/list";
    }

    @PostMapping("/new")
    public String createGoal(
            @Valid @ModelAttribute("savingsGoalDto") SavingsGoalDto savingsGoalDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        User user = authService.getCurrentUser();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid savings goal data");
            return "redirect:/savings";
        }

        try {
            savingsGoalService.createGoal(savingsGoalDto, user);
            redirectAttributes.addFlashAttribute("successMessage", "Savings goal created successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/savings";
    }

    @PostMapping("/{id}/edit")
    public String updateGoal(
            @PathVariable Long id,
            @Valid @ModelAttribute("savingsGoalDto") SavingsGoalDto savingsGoalDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        User user = authService.getCurrentUser();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid savings goal data");
            return "redirect:/savings";
        }

        try {
            savingsGoalService.updateGoal(id, savingsGoalDto, user);
            redirectAttributes.addFlashAttribute("successMessage", "Savings goal updated successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/savings";
    }

    @PostMapping("/{id}/delete")
    public String deleteGoal(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = authService.getCurrentUser();

        try {
            savingsGoalService.deleteGoal(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Savings goal deleted successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/savings";
    }

    @PostMapping("/{id}/contribute")
    public String addContribution(
            @PathVariable Long id,
            @Valid @ModelAttribute("savingsContributionDto") SavingsContributionDto savingsContributionDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        User user = authService.getCurrentUser();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid contribution data");
            return "redirect:/savings";
        }

        try {
            savingsGoalService.addContribution(id, savingsContributionDto, user);
            redirectAttributes.addFlashAttribute("successMessage", "Contribution added successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/savings";
    }

    @Data
    @AllArgsConstructor
    public static class GoalWithProgress {

        private SavingsGoal goal;
        private BigDecimal totalContributions;
        private BigDecimal progressPercentage;
        private List<SavingsContribution> contributions;
    }
}
