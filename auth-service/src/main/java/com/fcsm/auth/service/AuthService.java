package com.fcsm.auth.service;

import com.fcsm.auth.dto.LoginRequest;
import com.fcsm.auth.dto.LoginResponse;
import com.fcsm.auth.model.User;
import com.fcsm.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse authenticate(LoginRequest loginRequest) {
        System.out.println("=== AUTH SERVICE DEBUG ===");
        System.out.println("Authenticating user: " + loginRequest.getUsername());
        System.out.println("Plain password received: " + loginRequest.getPassword() + ", " + passwordEncoder.encode(loginRequest.getPassword()));

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            System.out.println("Authentication successful");
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lấy user từ authentication principal
            Object principal = authentication.getPrincipal();
            User user;

            if (principal instanceof User) {
                user = (User) principal;
            } else {
                // Nếu principal là UserPrincipal, cần get user từ đó
                user = userService.findByUsername(loginRequest.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found"));
            }

            System.out.println("User found: " + user.getUsername() + ", Role: " + user.getRole());

            String token = jwtUtil.generateToken(user.getUsername());
            System.out.println("Token generated successfully");

            return new LoginResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getDepartment(),
                    user.getRole()
            );
        } catch (Exception e) {
            System.out.println("Authentication failed in AuthService: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Logout user và blacklist token
     * @param token JWT token cần blacklist
     */
    public void logout(String token) {
        if (jwtUtil.validateToken(token)) {
            tokenBlacklistService.blacklistToken(token);
        }
    }

    /**
     * Refresh token - tạo token mới từ token cũ
     * @param oldToken Token cũ
     * @return LoginResponse với token mới
     */
    public LoginResponse refreshToken(String oldToken) {
        if (!jwtUtil.validateToken(oldToken)) {
            throw new RuntimeException("Invalid or expired token");
        }

        if (tokenBlacklistService.isTokenBlacklisted(oldToken)) {
            throw new RuntimeException("Token has been blacklisted");
        }

        String username = jwtUtil.getUsernameFromToken(oldToken);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo token mới
        String newToken = jwtUtil.generateToken(username);

        // Blacklist token cũ
        tokenBlacklistService.blacklistToken(oldToken);

        return new LoginResponse(
                newToken,
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getDepartment(),
                user.getRole()
        );
    }

    /**
     * Dùng để mã hóa password khi tạo user mới
     */
    public String generatePasswordHash(String password) {
        return passwordEncoder.encode(password);
    }
}