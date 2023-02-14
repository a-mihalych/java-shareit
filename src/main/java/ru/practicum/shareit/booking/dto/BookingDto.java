package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Integer id;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
    private User booker;
    private StatusBooking status;
}
