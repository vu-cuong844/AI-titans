package com.fcsm.auth.controller;

import com.fcsm.auth.dto.LoginRequest;
import com.fcsm.auth.dto.LoginResponse;
import com.fcsm.auth.model.Role;
import com.fcsm.auth.model.User;
import com.fcsm.auth.service.AuthService;
import com.fcsm.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private User testUser;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        loginResponse = new LoginResponse();
        loginResponse.setToken("jwt-token");
        loginResponse.setType("Bearer");
        loginResponse.setId(1L);
        loginResponse.setUsername("testuser");
        loginResponse.setFullName("Test User");
        loginResponse.setEmail("test@example.com");
        loginResponse.setDepartment("IT");
        loginResponse.setRole(Role.EMPLOYEE);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setDepartment("IT");
        testUser.setRole(Role.EMPLOYEE);
    }

    @Test
    void login_WithValidCredentials_ShouldReturnLoginResponse() throws Exception {
        // Given
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(loginResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.department").value("IT"))
                .andExpect(jsonPath("$.role").value("EMPLOYEE"));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnBadRequest() throws Exception {
        // Given
        when(authService.authenticate(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid username or password"));
    }

    @Test
    void getProfile_WithAuthenticatedUser_ShouldReturnUser() throws Exception {
        // Given
        when(authService.getCurrentUser()).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.department").value("IT"))
                .andExpect(jsonPath("$.role").value("EMPLOYEE"));
    }

    @Test
    void getProfile_WithNoAuthenticatedUser_ShouldReturnBadRequest() throws Exception {
        // Given
        when(authService.getCurrentUser()).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not authenticated"));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.findAll()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].fullName").value("Test User"));
    }

    @Test
    void getUsersByDepartment_ShouldReturnListOfUsers() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.findByDepartment("IT")).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/auth/users/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].department").value("IT"));
    }
}
