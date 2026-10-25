package ru.mfa.rbpo2026.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mfa.rbpo2026.domain.User;
public interface UserRepository extends JpaRepository<User,Long> { Optional<User> findByEmailIgnoreCase(String email); default Optional<User> findByEmail(String email){return findByEmailIgnoreCase(email);} }
