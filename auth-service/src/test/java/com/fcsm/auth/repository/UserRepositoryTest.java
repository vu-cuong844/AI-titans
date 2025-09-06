package com.fcsm.auth.repository;

import com.fcsm.auth.model.Role;
import com.fcsm.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        // Create test users
        testUser1 = new User();
        testUser1.setUsername("user1");
        testUser1.setPassword("password1");
        testUser1.setFullName("User One");
        testUser1.setEmail("user1@example.com");
        testUser1.setDepartment("IT");
        testUser1.setRole(Role.EMPLOYEE);
        testUser1.setIsActive(true);

        testUser2 = new User();
        testUser2.setUsername("user2");
        testUser2.setPassword("password2");
        testUser2.setFullName("User Two");
        testUser2.setEmail("user2@example.com");
        testUser2.setDepartment("HR");
        testUser2.setRole(Role.MANAGER);
        testUser2.setIsActive(true);

        testUser3 = new User();
        testUser3.setUsername("user3");
        testUser3.setPassword("password3");
        testUser3.setFullName("User Three");
        testUser3.setEmail("user3@example.com");
        testUser3.setDepartment("IT");
        testUser3.setRole(Role.EMPLOYEE);
        testUser3.setIsActive(false);

        // Persist test data
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
        entityManager.persistAndFlush(testUser3);
    }

    @Test
    void findByUsername_WithExistingUsername_ShouldReturnUser() {
        // Given
        String username = "user1";

        // When
        Optional<User> result = userRepository.findByUsername(username);

        // Then
        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        assertEquals("User One", result.get().getFullName());
        assertEquals("user1@example.com", result.get().getEmail());
        assertEquals("IT", result.get().getDepartment());
        assertEquals(Role.EMPLOYEE, result.get().getRole());
    }

    @Test
    void findByUsername_WithNonExistingUsername_ShouldReturnEmpty() {
        // Given
        String username = "nonexistent";

        // When
        Optional<User> result = userRepository.findByUsername(username);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findByEmail_WithExistingEmail_ShouldReturnUser() {
        // Given
        String email = "user2@example.com";

        // When
        Optional<User> result = userRepository.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        assertEquals("user2", result.get().getUsername());
        assertEquals("User Two", result.get().getFullName());
        assertEquals("HR", result.get().getDepartment());
        assertEquals(Role.MANAGER, result.get().getRole());
    }

    @Test
    void findByEmail_WithNonExistingEmail_ShouldReturnEmpty() {
        // Given
        String email = "nonexistent@example.com";

        // When
        Optional<User> result = userRepository.findByEmail(email);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void existsByUsername_WithExistingUsername_ShouldReturnTrue() {
        // Given
        String username = "user1";

        // When
        Boolean result = userRepository.existsByUsername(username);

        // Then
        assertTrue(result);
    }

    @Test
    void existsByUsername_WithNonExistingUsername_ShouldReturnFalse() {
        // Given
        String username = "nonexistent";

        // When
        Boolean result = userRepository.existsByUsername(username);

        // Then
        assertFalse(result);
    }

    @Test
    void existsByEmail_WithExistingEmail_ShouldReturnTrue() {
        // Given
        String email = "user2@example.com";

        // When
        Boolean result = userRepository.existsByEmail(email);

        // Then
        assertTrue(result);
    }

    @Test
    void existsByEmail_WithNonExistingEmail_ShouldReturnFalse() {
        // Given
        String email = "nonexistent@example.com";

        // When
        Boolean result = userRepository.existsByEmail(email);

        // Then
        assertFalse(result);
    }

    @Test
    void findByDepartment_WithExistingDepartment_ShouldReturnUsers() {
        // Given
        String department = "IT";

        // When
        List<User> result = userRepository.findByDepartment(department);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // user1 and user3
        assertTrue(result.stream().allMatch(user -> user.getDepartment().equals(department)));
    }

    @Test
    void findByDepartment_WithNonExistingDepartment_ShouldReturnEmptyList() {
        // Given
        String department = "Finance";

        // When
        List<User> result = userRepository.findByDepartment(department);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        // When
        List<User> result = userRepository.findAll();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(user -> user.getUsername().equals("user1")));
        assertTrue(result.stream().anyMatch(user -> user.getUsername().equals("user2")));
        assertTrue(result.stream().anyMatch(user -> user.getUsername().equals("user3")));
    }

    @Test
    void save_WithNewUser_ShouldPersistUser() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");
        newUser.setFullName("New User");
        newUser.setEmail("newuser@example.com");
        newUser.setDepartment("Finance");
        newUser.setRole(Role.EMPLOYEE);
        newUser.setIsActive(true);

        // When
        User savedUser = userRepository.save(newUser);

        // Then
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("New User", savedUser.getFullName());
        assertEquals("newuser@example.com", savedUser.getEmail());
        assertEquals("Finance", savedUser.getDepartment());
        assertEquals(Role.EMPLOYEE, savedUser.getRole());
        assertTrue(savedUser.getIsActive());
    }

    @Test
    void save_WithExistingUser_ShouldUpdateUser() {
        // Given
        testUser1.setFullName("Updated User One");
        testUser1.setEmail("updated@example.com");

        // When
        User updatedUser = userRepository.save(testUser1);

        // Then
        assertNotNull(updatedUser);
        assertEquals(testUser1.getId(), updatedUser.getId());
        assertEquals("Updated User One", updatedUser.getFullName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void delete_WithExistingUser_ShouldRemoveUser() {
        // Given
        Long userId = testUser1.getId();

        // When
        userRepository.delete(testUser1);

        // Then
        Optional<User> result = userRepository.findById(userId);
        assertFalse(result.isPresent());
    }
}
