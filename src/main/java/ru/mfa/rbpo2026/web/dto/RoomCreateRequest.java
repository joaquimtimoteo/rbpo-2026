package ru.mfa.rbpo2026.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RoomCreateRequest(
        @NotBlank(message = "не должно быть пустым") String number,
        @NotNull(message = "обязательное поле") @Positive(message = "должно быть больше нуля") Integer capacity,
        Boolean available) {
}
