package ru.mfa.rbpo2026.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.rbpo2026.common.ConflictException;
import ru.mfa.rbpo2026.common.NotFoundException;
import ru.mfa.rbpo2026.domain.User;
import ru.mfa.rbpo2026.repository.UserRepository;

/** Логика работы с пользователями системы. */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final HousingApplicationService applicationService;

    public UserService(UserRepository userRepository, HousingApplicationService applicationService) {
        this.userRepository = userRepository;
        this.applicationService = applicationService;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь " + id + " не найден"));
    }

    public User create(String fullName, String email) {
        checkEmailIsFree(email, null);

        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        return userRepository.save(user);
    }

    public User update(Long id, String fullName, String email) {
        User user = findById(id);
        if (fullName != null) {
            if (fullName.isBlank()) {
                throw new ConflictException("Имя пользователя не может быть пустым");
            }
            user.setFullName(fullName.trim());
        }
        if (email != null) {
            checkEmailIsFree(email, id);
            user.setEmail(email.trim());
        }
        return userRepository.save(user);
    }

    public void delete(Long id) {
        User user = findById(id);
        applicationService.deleteByApplicantId(user.getId());
        userRepository.deleteById(user.getId());
    }

    private void checkEmailIsFree(String email, Long currentUserId) {
        userRepository.findByEmail(email.trim()).ifPresent(existing -> {
            if (!existing.getId().equals(currentUserId)) {
                throw new ConflictException("Адрес " + email + " уже используется");
            }
        });
    }
}
