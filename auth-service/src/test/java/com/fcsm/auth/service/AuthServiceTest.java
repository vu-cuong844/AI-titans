package com.fcsm.auth.service;

import com.fcsm.auth.dto.LoginRequest;
import com.fcsm.auth.dto.LoginResponse;
import com.fcsm.auth.model.Role;
import com.fcsm.auth.model.User;
import com.fcsm.auth.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private User testUser;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setDepartment("IT");
        testUser.setRole(Role.EMPLOYEE);

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
    }

    @Test
    void authenticate_WithValidCredentials_ShouldReturnLoginResponse() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken("testuser")).thenReturn("jwt-token");

        // When
        LoginResponse result = authService.authenticate(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals("Bearer", result.getType());
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("Test User", result.getFullName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("IT", result.getDepartment());
        assertEquals(Role.EMPLOYEE, result.getRole());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken("testuser");
    }

    @Test
    void getCurrentUser_WithAuthenticatedUser_ShouldReturnUser() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When
        User result = authService.getCurrentUser();

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getFullName(), result.getFullName());
    }

    @Test
    void getCurrentUser_WithNoAuthentication_ShouldReturnNull() {
        // Given
        SecurityContextHolder.clearContext();

        // When
        User result = authService.getCurrentUser();

        // Then
        assertNull(result);
    }

    @Test
    void getCurrentUser_WithNonUserPrincipal_ShouldReturnNull() {
        // Given
        Authentication nonUserAuth = mock(Authentication.class);
        when(nonUserAuth.getPrincipal()).thenReturn("not-a-user");
        SecurityContextHolder.getContext().setAuthentication(nonUserAuth);

        // When
        User result = authService.getCurrentUser();

        // Then
        assertNull(result);
    }
}
