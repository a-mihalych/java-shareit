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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto addBooking(Integer userId, BookingNewDto bookingNewDto) {
        if (!bookingNewDto.getEnd().isAfter(bookingNewDto.getStart())) {
            throw new ValidationException(String.format("Создание прервано, " +
                                                        "дата начала %s должна быть раньше даты конца %s",
                                                        bookingNewDto.getStart(), bookingNewDto.getEnd()));
        }
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", userId));
        });
        Item item = itemRepository.findById(bookingNewDto.getItemId()).orElseThrow(() -> {
            throw new NotFoundException(String.format("Не найдена вещь с id = %d", bookingNewDto.getItemId()));
        });
        if (!item.getAvailable()) {
            throw new ValidationException("Создание прервано, вещь не доступна для бронирования");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Создание прервано, пользователь не должен бронировать свою вещь");
        }
        Booking booking = BookingMapper.toBooking(bookingNewDto, user, item, StatusBooking.WAITING);
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
    public List<BookingDto> bookingForUserId(Integer userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", userId));
        });
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                             userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, StatusBooking.WAITING);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, StatusBooking.REJECTED);
                break;
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            default:
                throw new ShareitException(String.format("Unknown state: %s", state));
        }
        return bookings.stream()
                       .map(BookingMapper::toBookingDto)
                       .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> bookingAllItemForUserId(Integer userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", userId));
        });
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.findAllCurrentForItemsOwner(userId, LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findAllPastForItemsOwner(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findAllForItemsOwnerByStatus(userId, StatusBooking.WAITING);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllFutureForItemsOwner(userId, LocalDateTime.now());
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllForItemsOwnerByStatus(userId, StatusBooking.REJECTED);
                break;
            case "ALL":
                bookings = bookingRepository.findAllForItemsOwner(userId);
                break;
            default:
                throw new ShareitException(String.format("Unknown state: %s", state));
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
