package ru.mfa.rbpo2026.service;

import org.springframework.stereotype.Service;
import ru.mfa.rbpo2026.common.ConflictException;
import ru.mfa.rbpo2026.domain.Room;
import ru.mfa.rbpo2026.repository.ResidenceContractRepository;

/** Расчёт занятости комнат: одно место занимает один действующий договор. */
@Service
public class RoomOccupancyService {

    private final ResidenceContractRepository contractRepository;

    public RoomOccupancyService(ResidenceContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    public long occupiedPlaces(Long roomId) {
        return contractRepository.countActiveByRoomId(roomId);
    }

    public long freePlaces(Room room) {
        return room.getCapacity() - occupiedPlaces(room.getId());
    }

    /** Проверяет, что в комнату можно заселить ещё одного проживающего. */
    public void ensureFreePlace(Room room) {
        if (!room.isAvailable()) {
            throw new ConflictException("Комната " + room.getNumber() + " недоступна для заселения");
        }
        if (freePlaces(room) <= 0) {
            throw new ConflictException("В комнате " + room.getNumber() + " нет свободных мест");
        }
    }
}
