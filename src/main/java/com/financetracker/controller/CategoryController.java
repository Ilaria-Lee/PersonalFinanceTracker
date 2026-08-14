// FILE: src/main/java/com/financetracker/controller/CategoryController.java
package com.financetracker.controller;

import com.financetracker.dto.CategoryDto;
import com.financetracker.exception.ValidationException;
import com.financetracker.model.User;
import com.financetracker.service.AuthService;
import com.financetracker.service.CategoryService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final AuthService authService;

    @GetMapping
    public String listCategories(Model model) {
        User user = authService.getCurrentUser();
        model.addAttribute("categories", categoryService.getCategoriesForUser(user));
        model.addAttribute("categoryDto", new CategoryDto());
        return "category/list";
    }

    @PostMapping("/new")
    public String createCategory(
            @Valid @ModelAttribute("categoryDto") CategoryDto categoryDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        User user = authService.getCurrentUser();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid category data");
            return "redirect:/categories";
        }

        try {
            categoryService.createCategory(categoryDto, user);
            redirectAttributes.addFlashAttribute("successMessage", "Category created successfully");
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = authService.getCurrentUser();

        try {
            categoryService.deleteCategory(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully");
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete: category is in use");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/categories";
    }
}
