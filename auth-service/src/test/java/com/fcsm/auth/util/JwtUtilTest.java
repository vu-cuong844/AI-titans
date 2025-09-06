package com.fcsm.auth.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Set test secret and expiration
        System.setProperty("jwt.secret", "testSecretKeyForJWTTokenGenerationAndValidation");
        System.setProperty("jwt.expiration", "86400000"); // 24 hours
    }

    @Test
    void generateToken_WithValidUsername_ShouldReturnToken() {
        // Given
        String username = "testuser";

        // When
        String token = jwtUtil.generateToken(username);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUsernameFromToken_WithValidToken_ShouldReturnUsername() {
        // Given
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // When
        String result = jwtUtil.getUsernameFromToken(token);

        // Then
        assertEquals(username, result);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Given
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // When
        boolean result = jwtUtil.validateToken(token);

        // Then
        assertTrue(result);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean result = jwtUtil.validateToken(invalidToken);

        // Then
        assertFalse(result);
    }

    @Test
    void validateToken_WithNullToken_ShouldReturnFalse() {
        // Given
        String nullToken = null;

        // When
        boolean result = jwtUtil.validateToken(nullToken);

        // Then
        assertFalse(result);
    }

    @Test
    void validateToken_WithEmptyToken_ShouldReturnFalse() {
        // Given
        String emptyToken = "";

        // When
        boolean result = jwtUtil.validateToken(emptyToken);

        // Then
        assertFalse(result);
    }

    @Test
    void isTokenExpired_WithValidToken_ShouldReturnFalse() {
        // Given
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // When
        boolean result = jwtUtil.isTokenExpired(token);

        // Then
        assertFalse(result);
    }

    @Test
    void isTokenExpired_WithInvalidToken_ShouldReturnTrue() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean result = jwtUtil.isTokenExpired(invalidToken);

        // Then
        assertTrue(result);
    }

    @Test
    void generateToken_WithDifferentUsernames_ShouldReturnDifferentTokens() {
        // Given
        String username1 = "user1";
        String username2 = "user2";

        // When
        String token1 = jwtUtil.generateToken(username1);
        String token2 = jwtUtil.generateToken(username2);

        // Then
        assertNotEquals(token1, token2);
    }

    @Test
    void getUsernameFromToken_WithDifferentTokens_ShouldReturnCorrectUsernames() {
        // Given
        String username1 = "user1";
        String username2 = "user2";
        String token1 = jwtUtil.generateToken(username1);
        String token2 = jwtUtil.generateToken(username2);

        // When
        String result1 = jwtUtil.getUsernameFromToken(token1);
        String result2 = jwtUtil.getUsernameFromToken(token2);

        // Then
        assertEquals(username1, result1);
        assertEquals(username2, result2);
    }
}
