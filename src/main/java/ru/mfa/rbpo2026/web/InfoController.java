package ru.mfa.rbpo2026.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mfa.rbpo2026.service.DormitoryStatisticsService;
import ru.mfa.rbpo2026.web.dto.DormitoryStatisticsResponse;

/**
 * Информационные эндпоинты приложения.
 * Первый возвращает текст, второй - числовые показатели заселения.
 */
@RestController
@RequestMapping("/api/info")
public class InfoController {

    private final DormitoryStatisticsService statisticsService;

    public InfoController(DormitoryStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String description() {
        return "Сервис распределения мест в общежитии";
    }

    @GetMapping("/statistics")
    public DormitoryStatisticsResponse statistics() {
        return new DormitoryStatisticsResponse(
                statisticsService.rooms(),
                statisticsService.totalPlaces(),
                statisticsService.occupiedPlaces(),
                statisticsService.freePlaces(),
                statisticsService.activeContracts());
    }
}
