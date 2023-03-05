package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingNextDtoTest {

    @Autowired
    private JacksonTester<BookingNextDto> json;

    @Test
    void bookingNextDtoJson() throws IOException {
        User user = User.builder()
                .id(1)
                .name("UserName")
                .email("user@mail.com")
                .build();
        Booking booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .booker(user)
                .status(StatusBooking.ALL)
                .build();
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        BookingNextDto bookingNextDto = BookingMapper.toBookingNextDto(bookingDto);
        JsonContent<BookingNextDto> result = json.write(bookingNextDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingNextDto.getId());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(bookingNextDto.getBookerId());
        assertThat(result).extractingJsonPathStringValue("$.status")
                          .isEqualTo(bookingNextDto.getStatus().toString());
    }
}
