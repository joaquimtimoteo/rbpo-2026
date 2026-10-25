package ru.mfa.rbpo2026.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mfa.rbpo2026.domain.RoomTransferRequest;
public interface RoomTransferRequestRepository extends JpaRepository<RoomTransferRequest,Long> { List<RoomTransferRequest> findByContractId(Long contractId); List<RoomTransferRequest> findByTargetRoomId(Long targetRoomId); }
