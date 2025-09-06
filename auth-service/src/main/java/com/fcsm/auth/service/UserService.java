package com.fcsm.auth.service;

import com.fcsm.auth.dto.RegisterRequest;
import com.fcsm.auth.dto.RegisterResponse;
import com.fcsm.auth.model.Role;
import com.fcsm.auth.model.User;
import com.fcsm.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Đăng ký user mới
     */
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        System.out.println("=== USER REGISTRATION DEBUG ===");
        System.out.println("Registering user: " + registerRequest.getUsername());

        // 1. Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username '" + registerRequest.getUsername() + "' already exists");
        }

        // 2. Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email '" + registerRequest.getEmail() + "' already exists");
        }

        // 3. Tạo user mới
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());

        // 4. Mã hóa password - QUAN TRỌNG cho bảo mật
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        newUser.setPassword(encodedPassword);
        System.out.println("Password encoded successfully");

        // 5. Set các thông tin khác
        newUser.setFullName(registerRequest.getFullName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setDepartment(registerRequest.getDepartment());
        newUser.setRole(Role.valueOf(registerRequest.getRole()));
        newUser.setIsActive(true); // Mặc định active

        // 6. Lưu vào database
        User savedUser = userRepository.save(newUser);
        System.out.println("User saved successfully with ID: " + savedUser.getId());

        // 7. Tạo response (không trả về password)
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getDepartment(),
                savedUser.getRole().name(),
                "User registered successfully"
        );
    }

    /**
     * Kiểm tra username có tồn tại không
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Kiểm tra email có tồn tại không
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Tìm user theo username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Tìm user theo email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Lấy tất cả users
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Tìm users theo department
     */
    public List<User> findByDepartment(String department) {
        return userRepository.findByDepartment(department);
    }

    /**
     * Lưu user
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Cập nhật password user
     */
    public void updatePassword(String username, String newPassword) {
        User user = findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        System.out.println("Password updated for user: " + username);
    }

    /**
     * Deactivate user
     */
    public void deactivateUser(String username) {
        User user = findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        user.setIsActive(false);
        userRepository.save(user);

        System.out.println("User deactivated: " + username);
    }

    /**
     * Activate user
     */
    public void activateUser(String username) {
        User user = findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        user.setIsActive(true);
        userRepository.save(user);

        System.out.println("User activated: " + username);
    }
}