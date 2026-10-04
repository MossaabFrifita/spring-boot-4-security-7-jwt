
package fr.mossaab.security.booking;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingRequest {
    private Long roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
