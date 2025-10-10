package com.evmarket.trade.config;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // Run first
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            log.info("Creating admin account...");
            createAdminAccount();
        } else {
            log.info("Admin account already exists, skipping creation.");
        }
    }

    @Transactional
    protected void createAdminAccount() {
        // Create Admin User
        User admin = new User();
        admin.setUsername("admin");
        String rawPassword = "Admin@123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        admin.setPassword(encodedPassword);
        admin.setStatus("active");
        admin.setRole("admin");
        admin.setFullName("System Administrator");
        admin.setEmail("admin@evmarket.com");
        admin.setPhone("0123456789");
        admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
        admin.setGender("male");
        admin.setIdentityCard("123456789");
        admin.setAddress("System Address");
        
        // Save the admin user
        admin = userRepository.save(admin);
        log.info("Created admin account with ID: {}", admin.getUserId());
        
        verifyAdminAccount(admin);
    }

    private void verifyAdminAccount(User admin) {
        log.info("Verifying admin account...");
        log.info("Username: {}", admin.getUsername());
        log.info("Status: {}", admin.getStatus());
        log.info("Role: {}", admin.getRole());
        log.info("Full Name: {}", admin.getFullName());
        log.info("Email: {}", admin.getEmail());
        log.info("Password matches: {}", passwordEncoder.matches("Admin@123", admin.getPassword()));
        log.info("Admin account created successfully!");
    }
}
