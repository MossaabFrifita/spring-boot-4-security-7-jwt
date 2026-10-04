package fr.mossaab.security.booking;

import fr.mossaab.security.room.Room;
import fr.mossaab.security.entities.User;
import fr.mossaab.security.enums.Role;
import fr.mossaab.security.repository.BookingRepository;
import fr.mossaab.security.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public Booking createBooking(BookingRequest request, User user) {
        if (request.getStartTime().isBefore(LocalDateTime.now()) ||
                request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("Некорректный интервал времени");
        }

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Комната не найдена"));

        if (room.isVipOnly() && !user.isVip()) {
            throw new IllegalStateException("Комната доступна только VIP пользователям");
        }

        boolean isOverlapping = bookingRepository.existsOverlappingBooking(
                room.getId(), request.getStartTime(), request.getEndTime());

        if (isOverlapping) {
            throw new IllegalStateException("Комната уже забронирована на выбранное время");
        }

        Booking booking = Booking.builder()
                .user(user)
                .room(room)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(BookingStatus.CONFIRMED)
                .build();

        return bookingRepository.save(booking);
    }

    public void cancelBooking(Long bookingId, User user) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Бронирование не найдено"));

        if (!booking.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new IllegalStateException("У вас нет прав для отмены этого бронирования");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(User user) {
        return bookingRepository.findByUserId(Math.toIntExact(user.getId()));
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
