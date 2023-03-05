package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.error.exception.ShareitException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.ShareItGateway.USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> bookingForUserId(@RequestHeader(USER_ID) long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.info("* Запрос Get: получение списка всех бронирований текущего пользователя, id = {}", userId);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new ShareitException("Unknown state: " + stateParam));
		return bookingClient.bookingForUserId(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID) long userId,
			@RequestBody @Valid BookingNewDto bookingNewDto) {
		log.info("* Запрос Post: добавление нового запроса на бронирование вещи {}, пользователем с id = {}",
				 bookingNewDto, userId);
		return bookingClient.addBooking(userId, bookingNewDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> bookingById(@RequestHeader(USER_ID) long userId,
			@PathVariable Long bookingId) {
		log.info("* Запрос Get: получение запроса бронирования по id = {}, пользователем с id = {}", bookingId, userId);
		return bookingClient.bookingById(userId, bookingId);
	}

    @GetMapping("/owner")
    public ResponseEntity<Object> bookingAllItemForUserId(@RequestHeader(USER_ID) Integer userId,
                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                    @RequestParam(name = "state", defaultValue = "all") String stateParam) {
		log.info("* Запрос Get: получение списка бронирований для всех вещей текущего пользователя, id = {}", userId);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new ShareitException("Unknown state: " + stateParam));
        return bookingClient.getBookingOwner(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(USER_ID) Integer userId,
                                    @PathVariable Integer bookingId,
                                    @RequestParam(name = "approved") Boolean approved) {
        log.info("* Запрос Patch: подтверждение/отклонение бронирования вещи c id = {}," +
                 " статус = {}, пользователь c id = {}", bookingId, approved, userId);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }
}
