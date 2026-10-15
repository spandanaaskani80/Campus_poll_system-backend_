package com.campuspoll.campus_poll_system.config;

import com.campuspoll.campus_poll_system.entity.User;
import com.campuspoll.campus_poll_system.repository.PollRepository;
import com.campuspoll.campus_poll_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PollRepository pollRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PollRepository pollRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.pollRepository = pollRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Ensure default administrator exists and is accessible
        userRepository.findByEmail("admin@campus.edu").ifPresentOrElse(admin -> {
            // Update admin password to known Admin@123 for seamless login
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            if (admin.getStudentId() == null || admin.getStudentId().isBlank()) {
                admin.setStudentId("ADM2026001");
            }
            userRepository.save(admin);
            logger.info("Administrator account initialized: admin@campus.edu");
        }, () -> {
            User newAdmin = new User();
            newAdmin.setName("Campus Administrator");
            newAdmin.setStudentId("ADM2026001");
            newAdmin.setEmail("admin@campus.edu");
            newAdmin.setPassword(passwordEncoder.encode("Admin@123"));
            newAdmin.setRole("ADMIN");
            userRepository.save(newAdmin);
            logger.info("Created default administrator: admin@campus.edu");
        });

        // Ensure default student demo accounts have known password Student@123
        userRepository.findByEmail("student1@campus.edu").ifPresent(student -> {
            student.setPassword(passwordEncoder.encode("Student@123"));
            if (student.getStudentId() == null || student.getStudentId().isBlank()) {
                student.setStudentId("STU2026001");
            }
            userRepository.save(student);
        });

        userRepository.findByEmail("student2@campus.edu").ifPresent(student -> {
            student.setPassword(passwordEncoder.encode("Student@123"));
            if (student.getStudentId() == null || student.getStudentId().isBlank()) {
                student.setStudentId("STU2026002");
            }
            userRepository.save(student);
        });

        userRepository.findByEmail("student3@campus.edu").ifPresent(student -> {
            student.setPassword(passwordEncoder.encode("Student@123"));
            if (student.getStudentId() == null || student.getStudentId().isBlank()) {
                student.setStudentId("STU2026003");
            }
            userRepository.save(student);
        });

        logger.info("Campus Poll System seed data verified.");
    }
}
