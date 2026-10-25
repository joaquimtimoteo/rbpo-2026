package ru.mfa.rbpo2026.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.rbpo2026.common.ConflictException;
import ru.mfa.rbpo2026.common.NotFoundException;
import ru.mfa.rbpo2026.domain.Room;
import ru.mfa.rbpo2026.repository.RoomRepository;

/** Логика работы с комнатами общежития. */
@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomOccupancyService occupancyService;
    private final ResidenceContractService contractService;
    private final RoomTransferRequestService transferRequestService;

    public RoomService(RoomRepository roomRepository,
                       RoomOccupancyService occupancyService,
                       ResidenceContractService contractService,
                       RoomTransferRequestService transferRequestService) {
        this.roomRepository = roomRepository;
        this.occupancyService = occupancyService;
        this.contractService = contractService;
        this.transferRequestService = transferRequestService;
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Комната " + id + " не найдена"));
    }

    public Room create(String number, int capacity, Boolean available) {
        checkNumberIsFree(number, null);

        Room room = new Room();
        room.setNumber(number.trim());
        room.setCapacity(capacity);
        room.setAvailable(available == null || available);
        return roomRepository.save(room);
    }

    public Room update(Long id, String number, Integer capacity, Boolean available) {
        Room room = findById(id);
        if (number != null) {
            if (number.isBlank()) {
                throw new ConflictException("Номер комнаты не может быть пустым");
            }
            checkNumberIsFree(number, id);
            room.setNumber(number.trim());
        }
        if (capacity != null) {
            long occupied = occupancyService.occupiedPlaces(id);
            if (capacity < occupied) {
                throw new ConflictException("Вместимость меньше числа проживающих (" + occupied + ")");
            }
            room.setCapacity(capacity);
        }
        if (available != null) {
            room.setAvailable(available);
        }
        return roomRepository.save(room);
    }

    public void delete(Long id) {
        Room room = findById(id);
        contractService.deleteByRoomId(room.getId());
        transferRequestService.deleteByTargetRoomId(room.getId());
        roomRepository.deleteById(room.getId());
    }

    public long occupiedPlaces(Long roomId) {
        return occupancyService.occupiedPlaces(roomId);
    }

    public long freePlaces(Room room) {
        return occupancyService.freePlaces(room);
    }

    private void checkNumberIsFree(String number, Long currentRoomId) {
        roomRepository.findByNumber(number.trim()).ifPresent(existing -> {
            if (!existing.getId().equals(currentRoomId)) {
                throw new ConflictException("Комната с номером " + number + " уже существует");
            }
        });
    }
}
