// FILE: src/main/java/com/financetracker/controller/AuthController.java
package com.financetracker.controller;

import com.financetracker.dto.RegisterDto;
import com.financetracker.exception.ValidationException;
import com.financetracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerDto") RegisterDto registerDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> log.error("Registration binding error: {}", e.getDefaultMessage()));
            return "auth/register";
        }

        try {
            userService.register(registerDto);
            redirectAttributes.addFlashAttribute("registered", true);
            return "redirect:/auth/login";
        } catch (ValidationException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Registration failed";
            bindingResult.rejectValue("email", "error.email", msg);
            return "auth/register";
        }
    }
}
