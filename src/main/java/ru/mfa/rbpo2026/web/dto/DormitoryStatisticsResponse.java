package ru.mfa.rbpo2026.web.dto;

public record DormitoryStatisticsResponse(
        long rooms,
        long totalPlaces,
        long occupiedPlaces,
        long freePlaces,
        long activeContracts) {
}
