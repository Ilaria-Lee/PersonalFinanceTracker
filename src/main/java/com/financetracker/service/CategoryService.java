// FILE: src/main/java/com/financetracker/service/CategoryService.java
package com.financetracker.service;

import com.financetracker.dto.CategoryDto;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.exception.UnauthorizedAccessException;
import com.financetracker.exception.ValidationException;
import com.financetracker.model.Category;
import com.financetracker.model.User;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.ExpenseRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    public List<Category> getCategoriesForUser(User user) {
        return categoryRepository.findByUser(user);
    }

    @SuppressWarnings("null")
    public Category createCategory(CategoryDto dto, User user) {
        if (categoryRepository.findByNameAndUser(dto.getName(), user).isPresent()) {
            throw new ValidationException("Category already exists");
        }

        Category category = Category.builder()
                .name(dto.getName())
                .user(user)
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Category created: {} for user {}", saved.getName(), user.getEmail());
        return saved;
    }

    public void createDefaultCategories(User user) {
        List<String> defaultCategories = Arrays.asList(
            "Cibo e bevande",
            "Trasporti",
            "Casa",
            "Salute",
            "Intrattenimento"
        );

        for (String name : defaultCategories) {
            try {
                createCategory(new CategoryDto(name), user);
            } catch (ValidationException ex) {
                // Skip duplicates for idempotent default-category creation.
            }
        }
    }

    public void deleteCategory(Long id, User user) {
        @SuppressWarnings("null")
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!Objects.equals(category.getUser().getId(), user.getId())) {
            throw new UnauthorizedAccessException("Unauthorized access to category");
        }

        if (expenseRepository.existsByCategory(category)) {
            throw new ValidationException("Category is in use");
        }

        categoryRepository.delete(category);
        log.info("Category deleted: {} for user {}", category.getName(), user.getEmail());
    }

    public Category findByIdAndUser(Long id, User user) {
        @SuppressWarnings("null")
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Long categoryUserId = category.getUser().getId();
        
        if (!Objects.equals(categoryUserId, user.getId())) {
            throw new UnauthorizedAccessException("Unauthorized access to category");
        }

        return category;
    }
}
