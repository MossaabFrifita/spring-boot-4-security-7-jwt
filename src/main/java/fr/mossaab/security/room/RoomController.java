package fr.mossaab.security.room;

import fr.mossaab.security.entities.User;
import fr.mossaab.security.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomRepository roomRepository;

    @GetMapping
    public ResponseEntity<List<Room>> getAvailableRooms(@AuthenticationPrincipal User user) {
        if (user.isVip()) {
            return ResponseEntity.ok(roomRepository.findAll());
        }
        return ResponseEntity.ok(roomRepository.findByIsVipOnlyFalse());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Room> createRoom(@RequestBody RoomRequest request) {
        Room room = Room.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .isVipOnly(request.isVipOnly())
                .build();
        return ResponseEntity.ok(roomRepository.save(room));
    }
}
