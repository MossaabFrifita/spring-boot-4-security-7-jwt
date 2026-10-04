package fr.mossaab.security.repository;

import fr.mossaab.security.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Integer userId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId " +
            "AND b.status = 'CONFIRMED' " +
            "AND (:startTime < b.endTime AND :endTime > b.startTime)")
    boolean existsOverlappingBooking(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
