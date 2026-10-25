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
import ru.mfa.rbpo2026.domain.RoomTransferRequest;
import ru.mfa.rbpo2026.service.RoomTransferRequestService;
import ru.mfa.rbpo2026.web.dto.RoomTransferCreateRequest;
import ru.mfa.rbpo2026.web.dto.RoomTransferResponse;
import ru.mfa.rbpo2026.web.dto.RoomTransferUpdateRequest;

@RestController
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
@RequestMapping("/api/room-transfer-requests")
public class RoomTransferRequestController {

    private final RoomTransferRequestService transferRequestService;

    public RoomTransferRequestController(RoomTransferRequestService transferRequestService) {
        this.transferRequestService = transferRequestService;
    }

    @GetMapping
    public List<RoomTransferResponse> findAll() {
        return transferRequestService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public RoomTransferResponse findById(@PathVariable Long id) {
        return toResponse(transferRequestService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomTransferResponse create(@Valid @RequestBody RoomTransferCreateRequest request) {
        return toResponse(transferRequestService.create(request.contractId(), request.targetRoomId()));
    }

    @PatchMapping("/{id}")
    public RoomTransferResponse update(@PathVariable Long id,
                                       @Valid @RequestBody RoomTransferUpdateRequest request) {
        return toResponse(transferRequestService.update(id, request.targetRoomId(), request.status()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        transferRequestService.delete(id);
    }

    private RoomTransferResponse toResponse(RoomTransferRequest request) {
        return new RoomTransferResponse(
                request.getId(),
                request.getContractId(),
                request.getTargetRoomId(),
                request.getSubmittedAt(),
                request.getStatus());
    }
}
