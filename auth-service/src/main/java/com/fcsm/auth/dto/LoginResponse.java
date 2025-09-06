package com.fcsm.auth.dto;

import com.fcsm.auth.model.Role;

public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String department;
    private Role role;
    
    public LoginResponse() {}
    
    public LoginResponse(String token, Long id, String username, String fullName, 
                        String email, String department, Role role) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.role = role;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
}
