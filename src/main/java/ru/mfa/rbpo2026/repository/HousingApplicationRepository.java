package ru.mfa.rbpo2026.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mfa.rbpo2026.domain.HousingApplication;
public interface HousingApplicationRepository extends JpaRepository<HousingApplication,Long> { List<HousingApplication> findByApplicantId(Long applicantId); }
