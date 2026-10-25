package ru.mfa.rbpo2026.web.dto;

import jakarta.validation.constraints.NotNull;

public record HousingApplicationCreateRequest(
        @NotNull(message = "обязательное поле") Long applicantId) {
}
