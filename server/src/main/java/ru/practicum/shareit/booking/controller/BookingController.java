package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exception.ShareitException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.ShareItApp.USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final ItemService itemService;

    @GetMapping
    public List<BookingDto> bookingForUserId(@RequestHeader(USER_ID) Integer userId,
                                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @RequestParam(name = "size", defaultValue = "10") Integer size,
                                             @RequestParam(name = "state", defaultValue = "ALL") String state) {
                log.info("* Запрос Get: получение списка всех бронирований текущего пользователя, id = {}", userId);
                StatusBooking status = StatusBooking.from(state);
                if (status == null) {
                    throw new ShareitException(String.format("Unknown state: %s", state));
                }
                return bookingService.bookingForUserId(userId, status, from, size);
            }

    @GetMapping("/owner")
    public List<BookingDto> bookingAllItemForUserId(@RequestHeader(USER_ID) Integer userId,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                    @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("* Запрос Get: получение списка бронирований для всех вещей текущего пользователя, id = {}", userId);
        StatusBooking status = StatusBooking.from(state);
        if (status == null) {
            throw new ShareitException(String.format("Unknown state: %s", state));
        }
        return bookingService.bookingAllItemForUserId(userId, status, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingDto bookingById(@RequestHeader(USER_ID) Integer userId, @PathVariable Integer bookingId) {
        log.info("* Запрос Get: получение запроса бронирования по id = {}, пользователем с id = {}", bookingId, userId);
        return bookingService.bookingById(userId, bookingId);
    }


    @PostMapping
    public BookingDto addBooking(@RequestHeader(USER_ID) Integer userId,
                                 @RequestBody BookingNewDto bookingNewDto) {
        log.info("* Запрос Post: добавление нового запроса на бронирование вещи {}, пользователем с id = {}",
                 bookingNewDto, userId);
        ItemDto itemDto = itemService.itemById(userId, bookingNewDto.getItemId());
        return bookingService.addBooking(userId, bookingNewDto, itemDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(USER_ID) Integer userId,
                                    @PathVariable Integer bookingId,
                                    @RequestParam(name = "approved") Boolean status) {
        log.info("* Запрос Patch: подтверждение/отклонение бронирования вещи c id = {}," +
                 " статус = {}, пользователь c id = {}", bookingId, status, userId);
        return bookingService.updateBooking(bookingId, userId, status ? StatusBooking.APPROVED : StatusBooking.REJECTED);
    }
}
