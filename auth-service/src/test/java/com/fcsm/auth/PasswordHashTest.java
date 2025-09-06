package com.fcsm.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {
    
    @Test
    public void testPasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password";
        String hashedPassword = encoder.encode(password);
        
        System.out.println("Original password: " + password);
        System.out.println("Hashed password: " + hashedPassword);
        
        // Verify
        boolean matches = encoder.matches(password, hashedPassword);
        System.out.println("Password matches: " + matches);
        
        // Test với hash hiện tại
        String currentHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi";
        boolean currentMatches = encoder.matches(password, currentHash);
        System.out.println("Current hash matches: " + currentMatches);
    }
}
