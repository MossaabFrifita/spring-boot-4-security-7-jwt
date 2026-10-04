
package fr.mossaab.security.room;

import lombok.Data;

@Data
public class RoomRequest {
    private String name;
    private Integer capacity;
    private boolean isVipOnly;
}