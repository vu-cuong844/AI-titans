package com.fcsm.auth.controller;

import com.fcsm.auth.dto.LoginRequest;
import com.fcsm.auth.dto.LoginResponse;
import com.fcsm.auth.dto.RegisterRequest;
import com.fcsm.auth.dto.RegisterResponse;
import com.fcsm.auth.model.User;
import com.fcsm.auth.service.AuthService;
import com.fcsm.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    /**
     * API Đăng nhập
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("=== LOGIN DEBUG ===");
            System.out.println("Username: " + loginRequest.getUsername());

            LoginResponse response = authService.authenticate(loginRequest);
            System.out.println("Login successful for user: " + response.getUsername());
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.core.AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Authentication failed", "Invalid username or password"));
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal server error", "Login failed: " + e.getMessage()));
        }
    }

    /**
     * API Đăng ký user mới
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest,
                                      BindingResult bindingResult) {
        try {
            System.out.println("=== REGISTER DEBUG ===");
            System.out.println("Registration request: " + registerRequest);

            // Kiểm tra validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage()));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createValidationErrorResponse("Validation failed", errors));
            }

            // Thực hiện đăng ký
            RegisterResponse response = userService.registerUser(registerRequest);
            System.out.println("Registration successful for user: " + response.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Registration failed", e.getMessage()));
        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal server error", "Registration failed: " + e.getMessage()));
        }
    }

    /**
     * API Kiểm tra username có tồn tại không
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<?> checkUsername(@PathVariable String username) {
        try {
            boolean exists = userService.existsByUsername(username);
            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("exists", exists);
            response.put("available", !exists);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal server error", e.getMessage()));
        }
    }

    /**
     * API Kiểm tra email có tồn tại không
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        try {
            boolean exists = userService.existsByEmail(email);
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("exists", exists);
            response.put("available", !exists);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal server error", e.getMessage()));
        }
    }

    /**
     * API Lấy thông tin profile user hiện tại
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            User user = authService.getCurrentUser();
            if (user != null) {
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Unauthorized", "User not authenticated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal server error", e.getMessage()));
        }
    }

    /**
     * API Lấy danh sách tất cả users (cần quyền ADMIN)
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            // Loại bỏ password khỏi response để bảo mật
            List<User> safeUsers = users.stream().map(this::sanitizeUser).collect(Collectors.toList());
            return ResponseEntity.ok(safeUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal server error", e.getMessage()));
        }
    }

    /**
     * API Lấy users theo department
     */
    @GetMapping("/users/department/{department}")
    public ResponseEntity<?> getUsersByDepartment(@PathVariable String department) {
        try {
            List<User> users = userService.findByDepartment(department);
            List<User> safeUsers = users.stream().map(this::sanitizeUser).collect(Collectors.toList());
            return ResponseEntity.ok(safeUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal server error", e.getMessage()));
        }
    }

    /**
     * API Logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                authService.logout(token);
                return ResponseEntity.ok(createSuccessResponse("Logout successful"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Bad request", "No valid token found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal server error", "Logout failed: " + e.getMessage()));
        }
    }

    /**
     * API Refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                LoginResponse response = authService.refreshToken(token);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Bad request", "No valid token found"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Unauthorized", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Internal server error", "Token refresh failed: " + e.getMessage()));
        }
    }

    // === UTILITY METHODS ===

    /**
     * Loại bỏ password khỏi User object để bảo mật
     */
    private User sanitizeUser(User user) {
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setFullName(user.getFullName());
        safeUser.setEmail(user.getEmail());
        safeUser.setDepartment(user.getDepartment());
        safeUser.setRole(user.getRole());
        safeUser.setIsActive(user.getIsActive());
        // Không set password để bảo mật
        return safeUser;
    }

    /**
     * Tạo error response chuẩn
     */
    private Map<String, Object> createErrorResponse(String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * Tạo validation error response
     */
    private Map<String, Object> createValidationErrorResponse(String error, Map<String, String> validationErrors) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        response.put("message", "Please check the input fields");
        response.put("validationErrors", validationErrors);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * Tạo success response chuẩn
     */
    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}