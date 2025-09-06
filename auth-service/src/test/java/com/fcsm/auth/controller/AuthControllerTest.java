package com.fcsm.auth.controller;

import com.fcsm.auth.dto.LoginRequest;
import com.fcsm.auth.dto.LoginResponse;
import com.fcsm.auth.model.Role;
import com.fcsm.auth.model.User;
import com.fcsm.auth.service.AuthService;
import com.fcsm.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testLoginSuccess() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        User user = new User("testuser", "password", "Test User", "test@company.com", "IT");
        user.setId(1L);
        user.setRole(Role.EMPLOYEE);
        
        LoginResponse loginResponse = new LoginResponse(
            "jwt-token", 1L, "testuser", "Test User", 
            "test@company.com", "IT", Role.EMPLOYEE
        );

        when(authService.authenticate(any(LoginRequest.class))).thenReturn(loginResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.fullName").value("Test User"));
    }

    @Test
    public void testLoginFailure() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("wronguser", "wrongpass");
        
        when(authService.authenticate(any(LoginRequest.class)))
            .thenThrow(new org.springframework.security.core.AuthenticationException("Invalid credentials") {});

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication failed"));
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        // Given
        String token = "valid-jwt-token";

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }

    @Test
    public void testLogoutWithoutToken() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad request"));
    }

    @Test
    public void testRefreshTokenSuccess() throws Exception {
        // Given
        String oldToken = "old-jwt-token";
        User user = new User("testuser", "password", "Test User", "test@company.com", "IT");
        user.setId(1L);
        user.setRole(Role.EMPLOYEE);
        
        LoginResponse newResponse = new LoginResponse(
            "new-jwt-token", 1L, "testuser", "Test User", 
            "test@company.com", "IT", Role.EMPLOYEE
        );

        when(authService.refreshToken(oldToken)).thenReturn(newResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + oldToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new-jwt-token"));
    }

    @Test
    public void testRefreshTokenFailure() throws Exception {
        // Given
        String invalidToken = "invalid-token";
        
        when(authService.refreshToken(invalidToken))
            .thenThrow(new RuntimeException("Invalid or expired token"));

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }
}
