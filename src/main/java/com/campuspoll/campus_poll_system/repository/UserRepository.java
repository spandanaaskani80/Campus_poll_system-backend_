package com.campuspoll.campus_poll_system.repository;

import com.campuspoll.campus_poll_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByStudentId(String studentId);
    boolean existsByEmail(String email);
    boolean existsByStudentId(String studentId);
    long countByRole(String role);
    List<User> findAllByOrderByCreatedAtDesc();
}
