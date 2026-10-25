package ru.mfa.rbpo2026.web.dto;

import java.time.LocalDate;
import ru.mfa.rbpo2026.domain.ApplicationStatus;

public record HousingApplicationResponse(
        Long id,
        Long applicantId,
        LocalDate submittedAt,
        ApplicationStatus status) {
}
