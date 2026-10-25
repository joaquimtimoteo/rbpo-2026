package ru.mfa.rbpo2026.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.rbpo2026.common.ConflictException;
import ru.mfa.rbpo2026.common.NotFoundException;
import ru.mfa.rbpo2026.domain.ContractStatus;
import ru.mfa.rbpo2026.domain.ResidenceContract;
import ru.mfa.rbpo2026.domain.Room;
import ru.mfa.rbpo2026.domain.RoomTransferRequest;
import ru.mfa.rbpo2026.domain.TransferStatus;
import ru.mfa.rbpo2026.repository.ResidenceContractRepository;
import ru.mfa.rbpo2026.repository.RoomRepository;
import ru.mfa.rbpo2026.repository.RoomTransferRequestRepository;

/** Логика работы с заявками на переселение. */
@Service
@Transactional
public class RoomTransferRequestService {

    private final RoomTransferRequestRepository requestRepository;
    private final ResidenceContractRepository contractRepository;
    private final RoomRepository roomRepository;
    private final RoomOccupancyService occupancyService;

    public RoomTransferRequestService(RoomTransferRequestRepository requestRepository,
                                      ResidenceContractRepository contractRepository,
                                      RoomRepository roomRepository,
                                      RoomOccupancyService occupancyService) {
        this.requestRepository = requestRepository;
        this.contractRepository = contractRepository;
        this.roomRepository = roomRepository;
        this.occupancyService = occupancyService;
    }

    public List<RoomTransferRequest> findAll() {
        return requestRepository.findAll();
    }

    public RoomTransferRequest findById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Заявка на переселение " + id + " не найдена"));
    }

    public RoomTransferRequest create(Long contractId, Long targetRoomId) {
        ResidenceContract contract = requireContract(contractId);
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new ConflictException("Переселение возможно только по действующему договору");
        }
        Room targetRoom = requireRoom(targetRoomId);
        if (targetRoom.getId().equals(contract.getRoomId())) {
            throw new ConflictException("Проживающий уже находится в комнате " + targetRoom.getNumber());
        }
        occupancyService.ensureFreePlace(targetRoom);

        RoomTransferRequest request = new RoomTransferRequest();
        request.setContractId(contractId);
        request.setTargetRoomId(targetRoomId);
        request.setSubmittedAt(LocalDate.now());
        request.setStatus(TransferStatus.NEW);
        return requestRepository.save(request);
    }

    public RoomTransferRequest update(Long id, Long targetRoomId, TransferStatus status) {
        RoomTransferRequest request = findById(id);
        if ((targetRoomId != null || status != null) && request.getStatus() != TransferStatus.NEW) {
            throw new ConflictException("Заявка уже обработана и не может быть изменена");
        }
        if (targetRoomId != null) {
            ResidenceContract contract = requireContract(request.getContractId());
            Room targetRoom = requireRoom(targetRoomId);
            if (targetRoom.getId().equals(contract.getRoomId())) {
                throw new ConflictException("Проживающий уже находится в комнате " + targetRoom.getNumber());
            }
            request.setTargetRoomId(targetRoomId);
        }
        if (status == TransferStatus.APPROVED) {
            approve(request);
        }
        if (status != null) {
            request.setStatus(status);
        }
        return requestRepository.save(request);
    }

    public void delete(Long id) {
        RoomTransferRequest request = findById(id);
        requestRepository.deleteById(request.getId());
    }

    /** Каскадное удаление заявок вместе с договором. */
    public void deleteByContractId(Long contractId) {
        requestRepository.findByContractId(contractId)
                .forEach(request -> requestRepository.deleteById(request.getId()));
    }

    /** Каскадное удаление заявок, нацеленных на удаляемую комнату. */
    public void deleteByTargetRoomId(Long roomId) {
        requestRepository.findByTargetRoomId(roomId)
                .forEach(request -> requestRepository.deleteById(request.getId()));
    }

    /** Одобрение заявки переносит проживающего в целевую комнату. */
    private void approve(RoomTransferRequest request) {
        ResidenceContract contract = requireContract(request.getContractId());
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new ConflictException("Договор уже расторгнут, переселение невозможно");
        }
        Room targetRoom = requireRoom(request.getTargetRoomId());
        occupancyService.ensureFreePlace(targetRoom);
        contract.setRoomId(targetRoom.getId());
        contractRepository.save(contract);
    }

    private ResidenceContract requireContract(Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Договор проживания " + contractId + " не найден"));
    }

    private Room requireRoom(Long roomId) {
        return roomRepository.findByIdForUpdate(roomId)
                .orElseThrow(() -> new NotFoundException("Комната " + roomId + " не найдена"));
    }
}
