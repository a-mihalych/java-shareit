package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingNextDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Integer requestId;
    private BookingNextDto nextBooking;
    private BookingNextDto lastBooking;
    private List<CommentDto> comments;
}
