package ru.mfa.rbpo2026.web.dto;

public record RoomResponse(
        Long id,
        String number,
        int capacity,
        boolean available,
        long occupiedPlaces,
        long freePlaces) {
}
