package ru.mfa.rbpo2026.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.rbpo2026.common.ConflictException;
import ru.mfa.rbpo2026.common.NotFoundException;
import ru.mfa.rbpo2026.domain.ApplicationStatus;
import ru.mfa.rbpo2026.domain.ContractStatus;
import ru.mfa.rbpo2026.domain.HousingApplication;
import ru.mfa.rbpo2026.domain.ResidenceContract;
import ru.mfa.rbpo2026.domain.Room;
import ru.mfa.rbpo2026.repository.HousingApplicationRepository;
import ru.mfa.rbpo2026.repository.ResidenceContractRepository;
import ru.mfa.rbpo2026.repository.RoomRepository;

/** Логика работы с договорами проживания. */
@Service
@Transactional
public class ResidenceContractService {

    private final ResidenceContractRepository contractRepository;
    private final HousingApplicationRepository applicationRepository;
    private final RoomRepository roomRepository;
    private final RoomOccupancyService occupancyService;
    private final RoomTransferRequestService transferRequestService;

    public ResidenceContractService(ResidenceContractRepository contractRepository,
                                    HousingApplicationRepository applicationRepository,
                                    RoomRepository roomRepository,
                                    RoomOccupancyService occupancyService,
                                    RoomTransferRequestService transferRequestService) {
        this.contractRepository = contractRepository;
        this.applicationRepository = applicationRepository;
        this.roomRepository = roomRepository;
        this.occupancyService = occupancyService;
        this.transferRequestService = transferRequestService;
    }

    public List<ResidenceContract> findAll() {
        return contractRepository.findAll();
    }

    public ResidenceContract findById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Договор проживания " + id + " не найден"));
    }

    public ResidenceContract create(Long applicationId, Long roomId, LocalDate moveInDate, LocalDate moveOutDate) {
        HousingApplication application = requireApplication(applicationId);
        if (application.getStatus() != ApplicationStatus.APPROVED) {
            throw new ConflictException("Договор заключается только по одобренному заявлению");
        }
        boolean alreadySettled = contractRepository.findByApplicationId(applicationId).stream()
                .anyMatch(contract -> contract.getStatus() == ContractStatus.ACTIVE);
        if (alreadySettled) {
            throw new ConflictException("По заявлению " + applicationId + " уже есть действующий договор");
        }
        Room room = requireRoom(roomId);
        occupancyService.ensureFreePlace(room);
        checkPeriod(moveInDate, moveOutDate);

        ResidenceContract contract = new ResidenceContract();
        contract.setApplicationId(applicationId);
        contract.setRoomId(roomId);
        contract.setMoveInDate(moveInDate);
        contract.setMoveOutDate(moveOutDate);
        contract.setStatus(ContractStatus.ACTIVE);
        return contractRepository.save(contract);
    }

    public ResidenceContract update(Long id, Long roomId, LocalDate moveOutDate, ContractStatus status) {
        ResidenceContract contract = findById(id);

        if (roomId != null && !roomId.equals(contract.getRoomId())) {
            Room room = requireRoom(roomId);
            if (contract.getStatus() == ContractStatus.ACTIVE) {
                occupancyService.ensureFreePlace(room);
            }
            contract.setRoomId(roomId);
        }
        if (moveOutDate != null) {
            checkPeriod(contract.getMoveInDate(), moveOutDate);
            contract.setMoveOutDate(moveOutDate);
        }
        if (status == ContractStatus.TERMINATED && contract.getStatus() == ContractStatus.ACTIVE) {
            contract.setStatus(ContractStatus.TERMINATED);
            if (contract.getMoveOutDate() == null) {
                contract.setMoveOutDate(LocalDate.now());
            }
        }
        if (status == ContractStatus.ACTIVE && contract.getStatus() == ContractStatus.TERMINATED) {
            occupancyService.ensureFreePlace(requireRoom(contract.getRoomId()));
            contract.setStatus(ContractStatus.ACTIVE);
        }
        return contractRepository.save(contract);
    }

    public void delete(Long id) {
        ResidenceContract contract = findById(id);
        transferRequestService.deleteByContractId(contract.getId());
        contractRepository.deleteById(contract.getId());
    }

    /** Каскадное удаление договоров вместе с заявлением. */
    public void deleteByApplicationId(Long applicationId) {
        contractRepository.findByApplicationId(applicationId)
                .forEach(contract -> delete(contract.getId()));
    }

    /** Каскадное удаление договоров вместе с комнатой. */
    public void deleteByRoomId(Long roomId) {
        contractRepository.findByRoomId(roomId)
                .forEach(contract -> delete(contract.getId()));
    }

    private void checkPeriod(LocalDate moveInDate, LocalDate moveOutDate) {
        if (moveOutDate != null && !moveOutDate.isAfter(moveInDate)) {
            throw new ConflictException("Дата выезда должна быть позже даты заселения");
        }
    }

    private HousingApplication requireApplication(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Заявление " + applicationId + " не найдено"));
    }

    private Room requireRoom(Long roomId) {
        return roomRepository.findByIdForUpdate(roomId)
                .orElseThrow(() -> new NotFoundException("Комната " + roomId + " не найдена"));
    }
}
