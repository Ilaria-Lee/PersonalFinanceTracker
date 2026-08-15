package com.financetracker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.financetracker.dto.RegisterDto;
import com.financetracker.exception.ValidationException;
import com.financetracker.model.User;
import com.financetracker.repository.UserRepository;
import com.financetracker.service.CategoryService;
import com.financetracker.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private CategoryService categoryService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
        userService.setCategoryService(categoryService);
    }

    @Test
    void register_successCreatesUserAndDefaultCategories() {
        RegisterDto dto = new RegisterDto("new.user@example.com", "SecurePass123!", "SecurePass123!");
        User savedUser = User.builder().id(1L).email(dto.getEmail()).password("encoded-password").build();

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        userService.register(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(dto.getEmail(), userCaptor.getValue().getEmail());
        assertEquals("encoded-password", userCaptor.getValue().getPassword());
        verify(categoryService).createDefaultCategories(savedUser);
    }

    @Test
    void register_rejectsMismatchedPasswords() {
        RegisterDto dto = new RegisterDto("new.user@example.com", "SecurePass123!", "DifferentPass123!");

        assertThrows(ValidationException.class, () -> userService.register(dto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_rejectsDuplicateEmail() {
        RegisterDto dto = new RegisterDto("existing@example.com", "SecurePass123!", "SecurePass123!");
        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(User.builder().id(1L).email(dto.getEmail()).build()));

        assertThrows(ValidationException.class, () -> userService.register(dto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loadUserByUsername_returnsStoredCredentials() {
        User user = User.builder().id(1L).email("user@example.com").password("encoded-password").build();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        org.springframework.security.core.userdetails.UserDetails result =
                userService.loadUserByUsername(user.getEmail());

        assertEquals(user.getEmail(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
    }
}
