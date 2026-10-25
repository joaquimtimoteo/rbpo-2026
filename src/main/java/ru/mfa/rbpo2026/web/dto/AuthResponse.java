package ru.mfa.rbpo2026.web.dto;

public record AuthResponse(Long id, String fullName, String email, String role, boolean enabled) {}
