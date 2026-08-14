// FILE: src/main/java/com/financetracker/service/AuthService.java
package com.financetracker.service;

import com.financetracker.exception.UnauthorizedAccessException;
import com.financetracker.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedAccessException("Not authenticated");
        }

        return userService.findByEmail(auth.getName());
    }
}
