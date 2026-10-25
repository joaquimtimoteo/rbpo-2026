package ru.mfa.rbpo2026.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mfa.rbpo2026.domain.ContractStatus;
import ru.mfa.rbpo2026.domain.ResidenceContract;
public interface ResidenceContractRepository extends JpaRepository<ResidenceContract,Long> {
 List<ResidenceContract> findByApplicationId(Long applicationId); List<ResidenceContract> findByRoomId(Long roomId);
 long countByRoomIdAndStatus(Long roomId, ContractStatus status);
 default long countActiveByRoomId(Long roomId){return countByRoomIdAndStatus(roomId,ContractStatus.ACTIVE);}
}
