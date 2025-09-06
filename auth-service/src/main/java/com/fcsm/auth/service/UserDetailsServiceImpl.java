package com.fcsm.auth.service;

import com.fcsm.auth.model.User;
import com.fcsm.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== USER DETAILS SERVICE DEBUG ===");
        System.out.println("Loading user by username: " + username);
        
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
            
            System.out.println("User found: " + user.getUsername());
            System.out.println("User active: " + user.getIsActive());
            System.out.println("User role: " + user.getRole());
            
            UserPrincipal userPrincipal = UserPrincipal.create(user);
            System.out.println("UserPrincipal created successfully");
            
            return userPrincipal;
        } catch (Exception e) {
            System.out.println("Error in loadUserByUsername: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
