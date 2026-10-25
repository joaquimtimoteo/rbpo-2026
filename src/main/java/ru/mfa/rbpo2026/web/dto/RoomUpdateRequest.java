package ru.mfa.rbpo2026.web.dto;

import jakarta.validation.constraints.Positive;

public record RoomUpdateRequest(
        String number,
        @Positive(message = "должно быть больше нуля") Integer capacity,
        Boolean available) {
}
