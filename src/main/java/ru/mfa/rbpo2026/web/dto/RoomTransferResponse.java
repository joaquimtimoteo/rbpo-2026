package ru.mfa.rbpo2026.web.dto;

import java.time.LocalDate;
import ru.mfa.rbpo2026.domain.TransferStatus;

public record RoomTransferResponse(
        Long id,
        Long contractId,
        Long targetRoomId,
        LocalDate submittedAt,
        TransferStatus status) {
}
