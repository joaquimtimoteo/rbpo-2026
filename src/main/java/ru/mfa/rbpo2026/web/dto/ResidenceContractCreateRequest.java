package ru.mfa.rbpo2026.web.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ResidenceContractCreateRequest(
        @NotNull(message = "обязательное поле") Long applicationId,
        @NotNull(message = "обязательное поле") Long roomId,
        @NotNull(message = "обязательное поле") LocalDate moveInDate,
        LocalDate moveOutDate) {
}
