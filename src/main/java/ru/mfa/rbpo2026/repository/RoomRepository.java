package ru.mfa.rbpo2026.repository;

import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mfa.rbpo2026.domain.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByNumberIgnoreCase(String number);
    default Optional<Room> findByNumber(String number) { return findByNumberIgnoreCase(number); }

    /** Locks a room row while its occupancy is checked and changed in the same transaction. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r where r.id = :id")
    Optional<Room> findByIdForUpdate(@Param("id") Long id);
}
