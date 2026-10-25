package ru.mfa.rbpo2026.web.dto;

import ru.mfa.rbpo2026.domain.TransferStatus;

public record RoomTransferUpdateRequest(Long targetRoomId, TransferStatus status) {
}
