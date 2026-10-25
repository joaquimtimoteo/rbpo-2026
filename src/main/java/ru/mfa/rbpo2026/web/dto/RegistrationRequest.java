package ru.mfa.rbpo2026.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank @Size(max=160) String fullName,
        @NotBlank @Email @Size(max=254) String email,
        @NotBlank @Size(min=12, max=72)
        @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])\\S+$", message="Пароль должен содержать строчную и заглавную буквы, цифру и специальный символ; пробелы запрещены")
        String password) {}
