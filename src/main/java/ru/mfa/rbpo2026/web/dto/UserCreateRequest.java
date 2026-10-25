package ru.mfa.rbpo2026.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
        @NotBlank(message = "не должно быть пустым") String fullName,
        @NotBlank(message = "не должно быть пустым") @Email(message = "некорректный адрес") String email) {
}
