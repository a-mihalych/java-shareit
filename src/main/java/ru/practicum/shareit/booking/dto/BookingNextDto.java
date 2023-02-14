package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BookingNextDto {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer bookerId;
    private StatusBooking status;
}
