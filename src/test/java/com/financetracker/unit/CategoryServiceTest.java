// FILE: src/test/java/com/financetracker/unit/CategoryServiceTest.java
package com.financetracker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.financetracker.dto.CategoryDto;
import com.financetracker.exception.UnauthorizedAccessException;
import com.financetracker.exception.ValidationException;
import com.financetracker.model.Category;
import com.financetracker.model.User;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.ExpenseRepository;
import com.financetracker.service.CategoryService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private CategoryService categoryService;

    @SuppressWarnings("null")
    @Test
    void createCategory_success() {
        User user = User.builder().id(1L).email("user@test.com").build();
        CategoryDto dto = new CategoryDto("Food");
        Category saved = Category.builder().id(10L).name("Food").user(user).build();

        when(categoryRepository.findByNameAndUser("Food", user)).thenReturn(Optional.empty());
        when(categoryRepository.save(org.mockito.ArgumentMatchers.any(Category.class))).thenReturn(saved);

        Category result = categoryService.createCategory(dto, user);

        assertNotNull(result);
        assertEquals("Food", result.getName());
        verify(categoryRepository).save(org.mockito.ArgumentMatchers.any(Category.class));
    }

    @Test
    void createCategory_duplicateName() {
        User user = User.builder().id(1L).build();
        CategoryDto dto = new CategoryDto("Food");
        Category existing = Category.builder().id(99L).name("Food").user(user).build();

        when(categoryRepository.findByNameAndUser("Food", user)).thenReturn(Optional.of(existing));

        assertThrows(ValidationException.class, () -> categoryService.createCategory(dto, user));
    }

    @Test
    void deleteCategory_inUse() {
        User user = User.builder().id(1L).build();
        Category category = Category.builder().id(10L).name("Food").user(user).build();

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(expenseRepository.existsByCategory(category)).thenReturn(true);

        assertThrows(ValidationException.class, () -> categoryService.deleteCategory(10L, user));
    }

    @Test
    void deleteCategory_wrongUser() {
        User currentUser = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Category category = Category.builder().id(10L).name("Food").user(owner).build();

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));

        assertThrows(UnauthorizedAccessException.class, () -> categoryService.deleteCategory(10L, currentUser));
    }
}
