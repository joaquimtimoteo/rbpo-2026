package ru.mfa.rbpo2026.web.dto;

import jakarta.validation.constraints.NotNull;

public record RoomTransferCreateRequest(
        @NotNull(message = "обязательное поле") Long contractId,
        @NotNull(message = "обязательное поле") Long targetRoomId) {
}
