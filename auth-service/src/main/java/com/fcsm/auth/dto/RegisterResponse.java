package com.fcsm.auth.dto;

public class RegisterResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String department;
    private String role;
    private boolean active;
    private String message;

    public RegisterResponse() {}

    public RegisterResponse(Long id, String username, String fullName,
                            String email, String department, String role, String message) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.role = role;
        this.active = true; // Mặc định active khi tạo mới
        this.message = message;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}