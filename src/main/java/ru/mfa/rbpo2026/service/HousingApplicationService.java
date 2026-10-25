package ru.mfa.rbpo2026.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.rbpo2026.common.ConflictException;
import ru.mfa.rbpo2026.common.NotFoundException;
import ru.mfa.rbpo2026.domain.ApplicationStatus;
import ru.mfa.rbpo2026.domain.HousingApplication;
import ru.mfa.rbpo2026.repository.HousingApplicationRepository;
import ru.mfa.rbpo2026.repository.UserRepository;

/** Логика работы с заявлениями на проживание. */
@Service
@Transactional
public class HousingApplicationService {

    private final HousingApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ResidenceContractService contractService;

    public HousingApplicationService(HousingApplicationRepository applicationRepository,
                                     UserRepository userRepository,
                                     ResidenceContractService contractService) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.contractService = contractService;
    }

    public List<HousingApplication> findAll() {
        return applicationRepository.findAll();
    }

    public HousingApplication findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Заявление " + id + " не найдено"));
    }

    public HousingApplication create(Long applicantId) {
        if (!userRepository.existsById(applicantId)) {
            throw new NotFoundException("Пользователь " + applicantId + " не найден");
        }
        boolean hasOpenApplication = applicationRepository.findByApplicantId(applicantId).stream()
                .anyMatch(application -> application.getStatus() == ApplicationStatus.NEW);
        if (hasOpenApplication) {
            throw new ConflictException("У пользователя " + applicantId + " уже есть нерассмотренное заявление");
        }

        HousingApplication application = new HousingApplication();
        application.setApplicantId(applicantId);
        application.setSubmittedAt(LocalDate.now());
        application.setStatus(ApplicationStatus.NEW);
        return applicationRepository.save(application);
    }

    public HousingApplication update(Long id, ApplicationStatus status) {
        HousingApplication application = findById(id);
        if (status != null && status != application.getStatus()) {
            if (application.getStatus() != ApplicationStatus.NEW) {
                throw new ConflictException("Заявление уже рассмотрено и не может быть изменено");
            }
            application.setStatus(status);
        }
        return applicationRepository.save(application);
    }

    public void delete(Long id) {
        HousingApplication application = findById(id);
        contractService.deleteByApplicationId(application.getId());
        applicationRepository.deleteById(application.getId());
    }

    /** Каскадное удаление заявлений вместе с пользователем. */
    public void deleteByApplicantId(Long applicantId) {
        applicationRepository.findByApplicantId(applicantId)
                .forEach(application -> delete(application.getId()));
    }
}
