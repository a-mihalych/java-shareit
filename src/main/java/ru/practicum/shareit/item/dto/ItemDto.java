package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingNextDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    Integer id;
    String name;
    String description;
    Boolean available;
    User owner;
    Integer requestId;
    BookingNextDto nextBooking;
    BookingNextDto lastBooking;
    List<CommentDto> comments;
}
