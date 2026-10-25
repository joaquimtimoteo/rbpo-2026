package ru.mfa.rbpo2026.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.mfa.rbpo2026.domain.User;
import ru.mfa.rbpo2026.repository.UserRepository;
import ru.mfa.rbpo2026.service.RegistrationService;
import ru.mfa.rbpo2026.web.dto.AuthResponse;
import ru.mfa.rbpo2026.web.dto.CsrfResponse;
import ru.mfa.rbpo2026.web.dto.RegistrationRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RegistrationService registrationService;
    private final UserRepository users;
    public AuthController(RegistrationService registrationService, UserRepository users) {
        this.registrationService = registrationService; this.users = users;
    }

    @GetMapping("/csrf")
    public CsrfResponse csrf(CsrfToken token) {
        return new CsrfResponse(token.getHeaderName(), token.getParameterName(), token.getToken());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegistrationRequest request) {
        return response(registrationService.register(request));
    }

    @GetMapping("/me")
    public AuthResponse me(Authentication authentication) {
        User user = users.findByEmailIgnoreCase(authentication.getName()).orElseThrow();
        return response(user);
    }

    private AuthResponse response(User user) {
        return new AuthResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name(), user.isEnabled());
    }
}
