package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(Integer userId, BookingNewDto bookingNewDto);

    BookingDto updateBooking(Integer bookingId, Integer userId, StatusBooking status);

    BookingDto bookingById(Integer userId, Integer bookingId);

    List<BookingDto> bookingForUserId(Integer userId, String state);

    List<BookingDto> bookingAllItemForUserId(Integer userId, String state);
}
