// FILE: src/main/java/com/financetracker/controller/BudgetController.java
package com.financetracker.controller;

import com.financetracker.dto.BudgetDto;
import com.financetracker.dto.BudgetProgressDto;
import com.financetracker.model.User;
import com.financetracker.service.AuthService;
import com.financetracker.service.BudgetService;
import com.financetracker.service.CategoryService;
import jakarta.validation.Valid;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final CategoryService categoryService;
    private final AuthService authService;

    @GetMapping
    public String listBudgets(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model
    ) {
        User user = authService.getCurrentUser();

        int targetMonth = (month != null) ? month : YearMonth.now().getMonthValue();
        int targetYear = (year != null) ? year : YearMonth.now().getYear();

        List<YearMonth> monthOptions = buildMonthOptions(targetMonth, targetYear);
        List<BudgetProgressDto> budgetProgress = budgetService.getBudgetProgress(user, targetMonth, targetYear);
        List<BudgetProgressDto> alertCategories = budgetProgress.stream()
            .filter(BudgetProgressDto::isAlertThresholdReached)
            .collect(Collectors.toList());

        model.addAttribute("budgetProgress", budgetProgress);
        model.addAttribute("alertCategories", alertCategories);
        model.addAttribute("categories", categoryService.getCategoriesForUser(user));
        model.addAttribute("monthOptions", monthOptions);
        model.addAttribute("selectedMonth", targetMonth);
        model.addAttribute("selectedYear", targetYear);

        if (!model.containsAttribute("budgetDto")) {
            BudgetDto budgetDto = new BudgetDto();
            budgetDto.setMonth(targetMonth);
            budgetDto.setYear(targetYear);
            model.addAttribute("budgetDto", budgetDto);
        }

        return "budget/list";
    }

    @PostMapping("/set")
    public String setBudget(
            @Valid @ModelAttribute("budgetDto") BudgetDto budgetDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        User user = authService.getCurrentUser();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid budget data");
            return "redirect:/budgets";
        }

        try {
            budgetService.setBudget(budgetDto, user);
            redirectAttributes.addFlashAttribute("successMessage", "Budget saved successfully");
            return "redirect:/budgets?month=" + budgetDto.getMonth() + "&year=" + budgetDto.getYear();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/budgets?month=" + budgetDto.getMonth() + "&year=" + budgetDto.getYear();
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteBudget(
            @PathVariable Long id,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            RedirectAttributes redirectAttributes
    ) {
        User user = authService.getCurrentUser();

        try {
            budgetService.deleteBudget(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Budget deleted successfully");
            
            if (month != null && year != null) {
                return "redirect:/budgets?month=" + month + "&year=" + year;
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/budgets";
    }

    private List<YearMonth> buildMonthOptions(int selectedMonth, int selectedYear) {
        YearMonth start = YearMonth.now();
        YearMonth selected = YearMonth.of(selectedYear, selectedMonth);
        List<YearMonth> options = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            options.add(start.plusMonths(i));
        }
        // Se il mese selezionato nel test è nel passato, lo aggiungiamo per renderlo visibile nella UI
        if (selected.isBefore(start) && !options.contains(selected)) {
            options.add(0, selected);
        }
        return options;
    }
}
