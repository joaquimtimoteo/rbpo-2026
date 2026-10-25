package ru.mfa.rbpo2026.web.dto;

import java.time.LocalDate;
import ru.mfa.rbpo2026.domain.ContractStatus;

public record ResidenceContractResponse(
        Long id,
        Long applicationId,
        Long roomId,
        LocalDate moveInDate,
        LocalDate moveOutDate,
        ContractStatus status) {
}
