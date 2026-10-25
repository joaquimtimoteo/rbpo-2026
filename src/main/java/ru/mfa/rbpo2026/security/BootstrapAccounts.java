package ru.mfa.rbpo2026.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.rbpo2026.domain.User;
import ru.mfa.rbpo2026.domain.UserRole;
import ru.mfa.rbpo2026.repository.UserRepository;

@Configuration
public class BootstrapAccounts {
    @Bean
    ApplicationRunner bootstrapPrivilegedAccounts(UserRepository users, PasswordEncoder encoder,
            @Value("${APP_ADMIN_NAME:}") String adminName, @Value("${APP_ADMIN_EMAIL:}") String adminEmail,
            @Value("${APP_ADMIN_PASSWORD:}") String adminPassword,
            @Value("${APP_STAFF_NAME:}") String staffName, @Value("${APP_STAFF_EMAIL:}") String staffEmail,
            @Value("${APP_STAFF_PASSWORD:}") String staffPassword) {
        return args -> {
            createIfConfigured(users, encoder, adminName, adminEmail, adminPassword, UserRole.ADMIN);
            createIfConfigured(users, encoder, staffName, staffEmail, staffPassword, UserRole.STAFF);
        };
    }

    private void createIfConfigured(UserRepository users, PasswordEncoder encoder, String name, String email, String password, UserRole role) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) return;
        String normalized = email.trim().toLowerCase(java.util.Locale.ROOT);
        if (users.findByEmailIgnoreCase(normalized).isPresent()) return;
        if (password.length() < 12 || password.chars().noneMatch(Character::isUpperCase)
                || password.chars().noneMatch(Character::isLowerCase) || password.chars().noneMatch(Character::isDigit)
                || password.chars().noneMatch(c -> !Character.isLetterOrDigit(c))) {
            throw new IllegalStateException("Configured bootstrap password does not meet strength requirements for " + role);
        }
        User user = new User(); user.setFullName(name == null || name.isBlank() ? role.name() : name.trim());
        user.setEmail(normalized); user.setPasswordHash(encoder.encode(password)); user.setRole(role);
        user.setEnabled(true); user.setAccountNonLocked(true); users.save(user);
    }
}
