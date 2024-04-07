package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.CommentDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentDto> comments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Booking {
        private Long id;
        private Long bookerId;
    }
}
