package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    @SneakyThrows
    void bookingDtoJson() {
        User user = User.builder()
                        .id(1)
                        .name("UserName")
                        .email("user@mail.com")
                        .build();
        Item item = Item.builder()
                        .id(1)
                        .name("nameItem")
                        .description("descriptionItem")
                        .available(true)
                        .owner(user)
                        .build();
        Booking booking = Booking.builder()
                                 .id(1)
                                 .item(item)
                                 .start(LocalDateTime.now())
                                 .end(LocalDateTime.now())
                                 .booker(user)
                                 .status(StatusBooking.ALL)
                                 .build();
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(bookingDto.getItem().getId());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                          .isEqualTo(bookingDto.getBooker().getId());
        assertThat(result).extractingJsonPathStringValue("$.status")
                          .isEqualTo(bookingDto.getStatus().toString());
    }
}