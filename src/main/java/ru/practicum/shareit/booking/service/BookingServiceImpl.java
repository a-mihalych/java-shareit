package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ShareitException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.StatusBooking.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;


    @Override
    @Transactional
    public BookingDto addBooking(Integer userId, BookingNewDto bookingNewDto, ItemDto itemDto) {
        if (!bookingNewDto.getEnd().isAfter(bookingNewDto.getStart())) {
            throw new ValidationException(String.format("Создание прервано, " +
                                                        "дата начала %s должна быть раньше даты конца %s",
                                                        bookingNewDto.getStart(), bookingNewDto.getEnd()));
        }
        UserDto userDto = userService.userById(userId);
        if (!itemDto.getAvailable()) {
            throw new ValidationException("Создание прервано, вещь не доступна для бронирования");
        }
        if (userId.equals(itemDto.getOwner().getId())) {
            throw new NotFoundException("Создание прервано, пользователь не должен бронировать свою вещь");
        }
        User user = UserMapper.toUser(userDto, new User());
        Item item = ItemMapper.toItem(itemDto, new Item());
        Booking booking = BookingMapper.toBooking(bookingNewDto, user, item, WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Integer bookingId, Integer userId, StatusBooking status) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Не найден запрос на бронирование с id = %d", bookingId));
        });
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Обновление прервано, вещь другого пользователя");
        }
        if (booking.getStatus() == status) {
            throw new ValidationException(String.format("Обновление прервано, статус не меняется %s", status));
        }
        booking.setStatus(status);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto bookingById(Integer userId, Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Не найден запрос на бронирование с id = %d", bookingId));
        });
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
                throw new NotFoundException(String.format(
                        "Пользователь с id = %d не является ни автором бронирования, ни владельцем вещи", userId));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> bookingForUserId(Integer userId, StatusBooking status) {
        userService.userById(userId);
        List<Booking> bookings;
        switch (status) {
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                             userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, WAITING);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, REJECTED);
                break;
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            default:
                throw new ShareitException(String.format("Unknown state: %s", status));
        }
        return bookings.stream()
                       .map(BookingMapper::toBookingDto)
                       .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> bookingAllItemForUserId(Integer userId, StatusBooking status) {
        userService.userById(userId);
        List<Booking> bookings;
        switch (status) {
            case CURRENT:
                bookings = bookingRepository.findAllCurrentForItemsOwner(userId, LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllPastForItemsOwner(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllForItemsOwnerByStatus(userId, WAITING);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureForItemsOwner(userId, LocalDateTime.now());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllForItemsOwnerByStatus(userId, REJECTED);
                break;
            case ALL:
                bookings = bookingRepository.findAllForItemsOwner(userId);
                break;
            default:
                throw new ShareitException(String.format("Unknown state: %s", status));
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> bookingItemForOwnerId(Integer userId, Integer itemId) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndItemOwnerIdOrderByStartDesc(itemId, userId);
        return bookings.stream()
                       .map(BookingMapper::toBookingDto)
                       .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> bookingItemForBookerId(Integer userId, Integer itemId, LocalDateTime now) {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndItemIdAndStartBefore(userId, itemId, now);
        return bookings.stream()
                       .map(BookingMapper::toBookingDto)
                       .collect(Collectors.toList());
    }
}
