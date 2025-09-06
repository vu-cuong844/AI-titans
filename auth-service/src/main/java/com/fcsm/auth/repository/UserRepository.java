// UserRepository.java - Enhanced version
package com.fcsm.auth.repository;

import com.fcsm.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Tìm user theo username
     */
    Optional<User> findByUsername(String username);

    /**
     * Tìm user theo email
     */
    Optional<User> findByEmail(String email);

    /**
     * Kiểm tra username có tồn tại không
     */
    boolean existsByUsername(String username);

    /**
     * Kiểm tra email có tồn tại không
     */
    boolean existsByEmail(String email);

    /**
     * Tìm users theo department
     */
    List<User> findByDepartment(String department);

    /**
     * Đếm số users theo role
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") String role);

    /**
     * Đếm số users theo department
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.department = :department")
    long countByDepartment(@Param("department") String department);

    /**
     * Tìm users theo partial username hoặc full name
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:search% OR u.fullName LIKE %:search%")
    List<User> searchByUsernameOrFullName(@Param("search") String search);

    /**
     * Tìm tất cả departments duy nhất
     */
    @Query("SELECT DISTINCT u.department FROM User u ORDER BY u.department")
    List<String> findAllDepartments();

    /**
     * Tìm tất cả roles duy nhất
     */
    @Query("SELECT DISTINCT u.role FROM User u ORDER BY u.role")
    List<String> findAllRoles();
}