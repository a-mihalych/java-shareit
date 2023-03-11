package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingDto addBooking(Integer userId, BookingNewDto bookingNewDto, ItemDto itemDto);

    BookingDto updateBooking(Integer bookingId, Integer userId, StatusBooking status);

    BookingDto bookingById(Integer userId, Integer bookingId);

    List<BookingDto> bookingForUserId(Integer userId, StatusBooking status, Integer from, Integer size);

    List<BookingDto> bookingAllItemForUserId(Integer userId, StatusBooking status, Integer from, Integer size);

    List<BookingDto> bookingItemForOwnerId(Integer userId, Integer itemId);

    List<BookingDto> bookingItemForBookerId(Integer userId, Integer itemId, LocalDateTime now);
}
