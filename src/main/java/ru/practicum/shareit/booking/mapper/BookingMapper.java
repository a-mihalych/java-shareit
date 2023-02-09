package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.dto.BookingNextDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(booking.getItem())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static BookingNextDto toBookingNextDto(Booking booking) {
        return BookingNextDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto, Booking booking) {
        return Booking.builder()
                .id(bookingDto.getId() != null ? bookingDto.getId() : booking.getId())
                .item(bookingDto.getItem() != null ? bookingDto.getItem() : booking.getItem())
                .start(bookingDto.getStart() != null ? bookingDto.getStart() : booking.getStart())
                .end(bookingDto.getEnd() != null ? bookingDto.getEnd() : booking.getEnd())
                .booker(bookingDto.getBooker() != null ? bookingDto.getBooker() : booking.getBooker())
                .status(bookingDto.getStatus() != null ? bookingDto.getStatus() : booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingNewDto bookingNewDto, User user, Item item, StatusBooking status) {
        return Booking.builder()
                .item(item)
                .start(bookingNewDto.getStart())
                .end(bookingNewDto.getEnd())
                .booker(user)
                .status(status)
                .build();
    }
}
