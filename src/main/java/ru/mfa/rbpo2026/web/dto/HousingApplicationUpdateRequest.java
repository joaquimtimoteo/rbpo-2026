package ru.mfa.rbpo2026.web.dto;

import ru.mfa.rbpo2026.domain.ApplicationStatus;

public record HousingApplicationUpdateRequest(ApplicationStatus status) {
}
