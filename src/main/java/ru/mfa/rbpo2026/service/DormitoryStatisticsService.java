package ru.mfa.rbpo2026.service;

import org.springframework.stereotype.Service;
import ru.mfa.rbpo2026.domain.ContractStatus;
import ru.mfa.rbpo2026.domain.Room;
import ru.mfa.rbpo2026.repository.ResidenceContractRepository;
import ru.mfa.rbpo2026.repository.RoomRepository;

/** Сводные показатели заселения общежития. */
@Service
public class DormitoryStatisticsService {

    private final RoomRepository roomRepository;
    private final ResidenceContractRepository contractRepository;

    public DormitoryStatisticsService(RoomRepository roomRepository,
                                      ResidenceContractRepository contractRepository) {
        this.roomRepository = roomRepository;
        this.contractRepository = contractRepository;
    }

    public long rooms() {
        return roomRepository.findAll().size();
    }

    public long totalPlaces() {
        return roomRepository.findAll().stream()
                .filter(Room::isAvailable)
                .mapToLong(Room::getCapacity)
                .sum();
    }

    public long occupiedPlaces() {
        return activeContracts();
    }

    public long freePlaces() {
        return totalPlaces() - occupiedPlaces();
    }

    public long activeContracts() {
        return contractRepository.findAll().stream()
                .filter(contract -> contract.getStatus() == ContractStatus.ACTIVE)
                .count();
    }
}
