// FILE: src/main/java/com/financetracker/controller/DashboardController.java
package com.financetracker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financetracker.dto.DashboardDto;
import com.financetracker.model.User;
import com.financetracker.service.AuthService;
import com.financetracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) throws JsonProcessingException {
        User user = authService.getCurrentUser();
        DashboardDto data = dashboardService.getDashboardData(user);

        String categoryDataJson = objectMapper.writeValueAsString(data.getExpensesByCategory());
        String monthlyDataJson = objectMapper.writeValueAsString(data.getLast6MonthsTotals());

        model.addAttribute("data", data);
        model.addAttribute("categoryDataJson", categoryDataJson);
        model.addAttribute("monthlyDataJson", monthlyDataJson);
        return "dashboard/index";
    }
}
