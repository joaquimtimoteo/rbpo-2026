package ru.mfa.rbpo2026.web;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.mfa.rbpo2026.domain.Room;
import ru.mfa.rbpo2026.service.RoomService;
import ru.mfa.rbpo2026.web.dto.RoomCreateRequest;
import ru.mfa.rbpo2026.web.dto.RoomResponse;
import ru.mfa.rbpo2026.web.dto.RoomUpdateRequest;

@RestController
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<RoomResponse> findAll() {
        return roomService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public RoomResponse findById(@PathVariable Long id) {
        return toResponse(roomService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse create(@Valid @RequestBody RoomCreateRequest request) {
        Room room = roomService.create(request.number(), request.capacity(), request.available());
        return toResponse(room);
    }

    @PatchMapping("/{id}")
    public RoomResponse update(@PathVariable Long id, @Valid @RequestBody RoomUpdateRequest request) {
        Room room = roomService.update(id, request.number(), request.capacity(), request.available());
        return toResponse(room);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        roomService.delete(id);
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getNumber(),
                room.getCapacity(),
                room.isAvailable(),
                roomService.occupiedPlaces(room.getId()),
                roomService.freePlaces(room));
    }
}
