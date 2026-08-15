package com.financetracker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.financetracker.exception.UnauthorizedAccessException;
import com.financetracker.model.User;
import com.financetracker.service.AuthService;
import com.financetracker.service.UserService;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUser_returnsAuthenticatedUser() {
        String email = "user@example.com";
        User user = User.builder().id(1L).email(email).build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, "password", Collections.emptyList())
        );
        when(userService.findByEmail(email)).thenReturn(user);

        User result = authService.getCurrentUser();

        assertEquals(user, result);
    }

    @Test
    void getCurrentUser_rejectsMissingAuthentication() {
        SecurityContextHolder.clearContext();

        assertThrows(UnauthorizedAccessException.class, authService::getCurrentUser);
    }
}
