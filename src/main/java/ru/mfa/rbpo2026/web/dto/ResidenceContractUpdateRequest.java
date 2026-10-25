package ru.mfa.rbpo2026.web.dto;

import java.time.LocalDate;
import ru.mfa.rbpo2026.domain.ContractStatus;

public record ResidenceContractUpdateRequest(
        Long roomId,
        LocalDate moveOutDate,
        ContractStatus status) {
}
