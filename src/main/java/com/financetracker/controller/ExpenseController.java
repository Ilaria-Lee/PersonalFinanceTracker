// FILE: src/main/java/com/financetracker/controller/ExpenseController.java
package com.financetracker.controller;

import com.financetracker.dto.ExpenseDto;
import com.financetracker.model.Expense;
import com.financetracker.model.User;
import com.financetracker.service.AuthService;
import com.financetracker.service.CategoryService;
import com.financetracker.service.ExpenseService;
import jakarta.validation.Valid;
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
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final AuthService authService;

    @GetMapping
    public String listExpenses(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model
    ) {
        User user = authService.getCurrentUser();
        model.addAttribute("expenses", expenseService.getExpensesFiltered(user, categoryId, month, year));
        model.addAttribute("categories", categoryService.getCategoriesForUser(user));
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        return "expense/list";
    }

    @GetMapping("/new")
    public String newExpenseForm(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("expenseDto", new ExpenseDto());
        model.addAttribute("categories", categoryService.getCategoriesForUser(user));
        return "expense/form";
    }

    @PostMapping("/new")
    public String createExpense(
            @Valid @ModelAttribute("expenseDto") ExpenseDto expenseDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        User user = authService.getCurrentUser();
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getCategoriesForUser(user));
            return "expense/form";
        }

        try {
            expenseService.createExpense(expenseDto, user);
            redirectAttributes.addFlashAttribute("successMessage", "Expense added successfully");
            System.out.println(">>> Flash attribute added, redirecting to /expenses");
            return "redirect:/expenses";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/expenses";
        }
    }

    @GetMapping("/{id}/edit")
    public String editExpenseForm(@PathVariable Long id, Model model) {
        User user = authService.getCurrentUser();
        Expense expense = expenseService.findByIdAndUser(id, user);
        System.out.println(">>> expense.getDate() = " + expense.getDate());
        ExpenseDto dto = expenseService.toDto(expense);
        System.out.println(">>> dto.getDate() = " + dto.getDate());
        
        model.addAttribute("expenseDto", dto);
        model.addAttribute("categories", categoryService.getCategoriesForUser(user));
        return "expense/form";
    }

    @PostMapping("/{id}/edit")
    public String updateExpense(
            @PathVariable Long id,
            @Valid @ModelAttribute("expenseDto") ExpenseDto expenseDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        User user = authService.getCurrentUser();
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getCategoriesForUser(user));
            return "expense/form";
        }

        try {
            expenseService.updateExpense(id, expenseDto, user);
            redirectAttributes.addFlashAttribute("successMessage", "Expense updated successfully");
            return "redirect:/expenses";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/expenses";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteExpense(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = authService.getCurrentUser();
        try {
            expenseService.deleteExpense(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Expense deleted successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/expenses";
    }
}
