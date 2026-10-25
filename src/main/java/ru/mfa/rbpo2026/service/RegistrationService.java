package ru.mfa.rbpo2026.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.rbpo2026.common.ConflictException;
import ru.mfa.rbpo2026.domain.User;
import ru.mfa.rbpo2026.domain.UserRole;
import ru.mfa.rbpo2026.repository.UserRepository;
import ru.mfa.rbpo2026.web.dto.RegistrationRequest;

@Service
public class RegistrationService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    public RegistrationService(UserRepository users, PasswordEncoder encoder) { this.users = users; this.encoder = encoder; }

    @Transactional
    public User register(RegistrationRequest request) {
        String email = request.email().trim().toLowerCase(java.util.Locale.ROOT);
        if (users.findByEmailIgnoreCase(email).isPresent()) throw new ConflictException("Учётная запись с таким email уже существует");
        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(request.password()));
        user.setRole(UserRole.STUDENT); // роль из запроса никогда не принимается
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        return users.save(user);
    }
}
