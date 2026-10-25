package ru.mfa.rbpo2026.web.dto;

import jakarta.validation.constraints.Email;

public record UserUpdateRequest(
        String fullName,
        @Email(message = "некорректный адрес") String email) {
}
