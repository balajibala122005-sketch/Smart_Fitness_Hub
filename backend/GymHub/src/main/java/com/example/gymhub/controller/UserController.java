package com.example.gymhub.controller;

import com.example.gymhub.dto.DashboardSummaryDto;
import com.example.gymhub.dto.UserDto;
import com.example.gymhub.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        UserDto userDto = userService.getUserProfile(authentication.getName());
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/dashboard-summary")
    public ResponseEntity<DashboardSummaryDto> getDashboardSummary(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        DashboardSummaryDto summary = userService.getDashboardSummary(authentication.getName());
        return ResponseEntity.ok(summary);
    }
}
