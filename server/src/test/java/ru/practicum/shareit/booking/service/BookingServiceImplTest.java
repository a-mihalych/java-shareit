package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private Item item;
    private User user1;
    private User user2;
    private UserDto user3Dto;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                    .id(1)
                    .name("User1Name")
                    .email("user1@mail.com")
                    .build();
        user2 = User.builder()
                    .id(2)
                    .name("User2Name")
                    .email("user2@mail.com")
                    .build();
        user3Dto = UserDto.builder()
                          .id(3)
                          .name("User3Name")
                          .email("user3@mail.com")
                          .build();
        item = Item.builder()
                   .id(1)
                   .name("nameItem")
                   .description("descriptionItem")
                   .available(true)
                   .owner(user1)
                   .build();
        booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(user2)
                .status(StatusBooking.ALL)
                .build();
    }

    @Test
    void addBooking_bookingDto() {
        BookingNewDto bookingNewDto = BookingNewDto.builder()
                                                   .itemId(item.getId())
                                                   .start(LocalDateTime.now().plusDays(1))
                                                   .end(LocalDateTime.now().plusDays(2))
                                                   .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto bookingDto = bookingService.addBooking(user2.getId(), bookingNewDto, itemDto);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void addBooking_validationException1() {
        BookingNewDto bookingNewDto = BookingNewDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertThrows(ValidationException.class, () -> bookingService.addBooking(user2.getId(), bookingNewDto, itemDto));
    }

    @Test
    void addBooking_validationException2() {
        BookingNewDto bookingNewDto = BookingNewDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setAvailable(false);
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        assertThrows(ValidationException.class, () -> bookingService.addBooking(user2.getId(), bookingNewDto, itemDto));
    }

    @Test
    void addBooking_notFoundException() {
        BookingNewDto bookingNewDto = BookingNewDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(user1.getId(), bookingNewDto, itemDto));
    }

    @Test
    void updateBooking_bookingDto() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto bookingDto = bookingService.updateBooking(booking.getId(), booking.getItem().getOwner().getId(),
                                                             StatusBooking.APPROVED);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void updateBooking_notFoundException1() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.updateBooking(booking.getId(),
                                                                                 booking.getItem().getOwner().getId(),
                                                                                 StatusBooking.APPROVED));
    }

    @Test
    void updateBooking_notFoundException2() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () -> bookingService.updateBooking(booking.getId(), user2.getId(),
                                                                                 StatusBooking.APPROVED));
    }

    @Test
    void updateBooking_validationException() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        assertThrows(ValidationException.class, () -> bookingService.updateBooking(booking.getId(),
                                                                                   booking.getItem().getOwner().getId(),
                                                                                   StatusBooking.ALL));
    }

    @Test
    void bookingById_bookingDto() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        BookingDto bookingDto = bookingService.bookingById(booking.getBooker().getId(), booking.getId());
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void bookingById_notFoundException1() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.bookingById(booking.getBooker().getId(),
                                                                               booking.getId() + 1));
    }

    @Test
    void bookingById_notFoundException2() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () -> bookingService.bookingById(user3Dto.getId(), booking.getId()));
    }

    @Test
    void bookingForUserId_listBookingDtoCURRENT() {
        Integer from = 0;
        Integer size = 10;
        PageImpl<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                               anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                              .thenReturn(page);
        List<BookingDto> bookingsDto = bookingService.bookingForUserId(
                booking.getBooker().getId(), StatusBooking.CURRENT, from, size);
        assertEquals(1, bookingsDto.size());
        assertEquals(booking.getId(), bookingsDto.get(0).getId());
        assertEquals(booking.getItem().getId(), bookingsDto.get(0).getItem().getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals(booking.getBooker().getId(), bookingsDto.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookingsDto.get(0).getStatus());
    }

    @Test
    void bookingForUserId_listBookingDtoPAST() {
        Integer from = 0;
        Integer size = 10;
        PageImpl<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyInt(), any(LocalDateTime.class),
                                                                             any(PageRequest.class)))
                                                                             .thenReturn(page);
        List<BookingDto> bookingsDto = bookingService.bookingForUserId(
                booking.getBooker().getId(), StatusBooking.PAST, from, size);
        assertEquals(1, bookingsDto.size());
        assertEquals(booking.getId(), bookingsDto.get(0).getId());
        assertEquals(booking.getItem().getId(), bookingsDto.get(0).getItem().getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals(booking.getBooker().getId(), bookingsDto.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookingsDto.get(0).getStatus());
    }

    @Test
    void bookingForUserId_listBookingDtoWAITING() {
        Integer from = 0;
        Integer size = 10;
        PageImpl<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyInt(), any(StatusBooking.class),
                                                                          any(PageRequest.class)))
                                                                          .thenReturn(page);
        List<BookingDto> bookingsDto = bookingService.bookingForUserId(
                booking.getBooker().getId(), StatusBooking.WAITING, from, size);
        assertEquals(1, bookingsDto.size());
        assertEquals(booking.getId(), bookingsDto.get(0).getId());
        assertEquals(booking.getItem().getId(), bookingsDto.get(0).getItem().getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals(booking.getBooker().getId(), bookingsDto.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookingsDto.get(0).getStatus());
    }

    @Test
    void bookingForUserId_listBookingDtoFUTURE() {
        Integer from = 0;
        Integer size = 10;
        PageImpl<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyInt(), any(LocalDateTime.class),
                                                                              any(PageRequest.class)))
                                                                              .thenReturn(page);
        List<BookingDto> bookingsDto = bookingService.bookingForUserId(
                booking.getBooker().getId(), StatusBooking.FUTURE, from, size);
        assertEquals(1, bookingsDto.size());
        assertEquals(booking.getId(), bookingsDto.get(0).getId());
        assertEquals(booking.getItem().getId(), bookingsDto.get(0).getItem().getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals(booking.getBooker().getId(), bookingsDto.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookingsDto.get(0).getStatus());
    }

    @Test
    void bookingForUserId_listBookingDtoREJECTED() {
        Integer from = 0;
        Integer size = 10;
        PageImpl<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyInt(), any(StatusBooking.class),
                                                                          any(PageRequest.class)))
                                                                          .thenReturn(page);
        List<BookingDto> bookingsDto = bookingService.bookingForUserId(
                booking.getBooker().getId(), StatusBooking.REJECTED, from, size);
        assertEquals(1, bookingsDto.size());
        assertEquals(booking.getId(), bookingsDto.get(0).getId());
        assertEquals(booking.getItem().getId(), bookingsDto.get(0).getItem().getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals(booking.getBooker().getId(), bookingsDto.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookingsDto.get(0).getStatus());
    }

    @Test
    void bookingForUserId_listBookingDtoALL() {
        Integer from = 0;
        Integer size = 10;
        PageImpl<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyInt(), any(PageRequest.class)))
                                                                 .thenReturn(page);
        List<BookingDto> bookingsDto = bookingService.bookingForUserId(
                booking.getBooker().getId(), StatusBooking.ALL, from, size);
        assertEquals(1, bookingsDto.size());
        assertEquals(booking.getId(), bookingsDto.get(0).getId());
        assertEquals(booking.getItem().getId(), bookingsDto.get(0).getItem().getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals(booking.getBooker().getId(), bookingsDto.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookingsDto.get(0).getStatus());
    }

    @Test
    void bookingAllItemForUserId_listBookingDtoCURRENT() {
        Integer from = 0;
        Integer size = 10;
        PageImpl<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        when(bookingRepository.findAllCurrentForItemsOwner(anyInt(), any(LocalDateTime.class), any(PageRequest.class)))
                                                           .thenReturn(page);
        List<BookingDto> bookingsDto = bookingService.bookingAllItemForUserId(
                booking.getBooker().getId(), StatusBooking.CURRENT, from, size);
        assertEquals(1, bookingsDto.size());
        assertEquals(booking.getId(), bookingsDto.get(0).getId());
        assertEquals(booking.getItem().getId(), bookingsDto.get(0).getItem().getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals(booking.getBooker().getId(), bookingsDto.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookingsDto.get(0).getStatus());
    }

    @Test
    void bookingAllItemForUserId_listBookingDtoPAST() {
        Integer from = 0;
        Integer size = 10;
        PageImpl<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        when(bookingRepository.findAllPastForItemsOwner(anyInt(), any(LocalDateTime.class), any(PageRequest.class)))
                                                        .thenReturn(page);
        List<BookingDto> bookingsDto = bookingService.bookingAllItemForUserId(
                booking.getBooker().getId(), StatusBooking.PAST, from, size);
        assertEquals(1, bookingsDto.size());
        assertEquals(booking.getId(), bookingsDto.get(0).getId());
        assertEquals(booking.getItem().getId(), bookingsDto.get(0).getItem().getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals(booking.getBooker().getId(), bookingsDto.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookingsDto.get(0).getStatus());
    }

    @Test
    void bookingAllItemForUserId_listBookingDtoWAITING() {
        Integer from = 0;
        Integer size = 10;
        PageImpl<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        when(bookingRepository.findAllForItemsOwnerByStatus(anyInt(), any(StatusBooking.class), any(PageRequest.class)))
                                                            .thenReturn(page);
        List<BookingDto> bookingsDto = bookingService.bookingAllItemForUserId(
                booking.getBooker().getId(), StatusBooking.WAITING, from, size);
        assertEquals(1, bookingsDto.size());
        assertEquals(booking.getId(), bookingsDto.get(0).getId());
        assertEquals(booking.getItem().getId(), bookingsDto.get(0).getItem().getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals(booking.getBooker().getId(), bookingsDto.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookingsDto.get(0).getStatus());
    }

    @Test
    void bookingAllItemForUserId_listBookingDtoFUTURE() {
        Integer from = 0;
        Integer size = 10;
        PageImpl<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        when(bookingRepository.findAllFutureForItemsOwner(anyInt(), any(LocalDateTime.class), any(PageRequest.class)))
                                                          .thenReturn(page);
        List<BookingDto> bookingsDto = bookingService.bookingAllItemForUserId(
                booking.getBooker().getId(), StatusBooking.FUTURE, from, size);
        assertEquals(1, bookingsDto.size());
        assertEquals(booking.getId(), bookingsDto.get(0).getId());
        assertEquals(booking.getItem().getId(), bookingsDto.get(0).getItem().getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals(booking.getBooker().getId(), bookingsDto.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookingsDto.get(0).getStatus());
    }

    @Test
    void bookingAllItemForUserId_listBookingDtoREJECTED() {
        Integer from = 0;
        Integer size = 10;
        PageImpl<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        when(bookingRepository.findAllForItemsOwnerByStatus(anyInt(), any(StatusBooking.class), any(PageRequest.class)))
                                                            .thenReturn(page);
        List<BookingDto> bookingsDto = bookingService.bookingAllItemForUserId(
                booking.getBooker().getId(), StatusBooking.REJECTED, from, size);
        assertEquals(1, bookingsDto.size());
        assertEquals(booking.getId(), bookingsDto.get(0).getId());
        assertEquals(booking.getItem().getId(), bookingsDto.get(0).getItem().getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals(booking.getBooker().getId(), bookingsDto.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookingsDto.get(0).getStatus());
    }

    @Test
    void bookingAllItemForUserId_listBookingDtoALL() {
        Integer from = 0;
        Integer size = 10;
        PageImpl<Booking> page = new PageImpl<>(List.of(booking));
        when(userService.userById(anyInt())).thenReturn(user3Dto);
        when(bookingRepository.findAllForItemsOwner(anyInt(), any(PageRequest.class))).thenReturn(page);
        List<BookingDto> bookingsDto = bookingService.bookingAllItemForUserId(
                booking.getBooker().getId(), StatusBooking.ALL, from, size);
        assertEquals(1, bookingsDto.size());
        assertEquals(booking.getId(), bookingsDto.get(0).getId());
        assertEquals(booking.getItem().getId(), bookingsDto.get(0).getItem().getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals(booking.getBooker().getId(), bookingsDto.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookingsDto.get(0).getStatus());
    }

    @Test
    void bookingItemForOwnerId_listBookingDto() {
        when(bookingRepository.findAllByItemIdAndItemOwnerIdOrderByStartDesc(anyInt(), anyInt()))
                                                                             .thenReturn(List.of(booking));
        List<BookingDto> bookingsDto = bookingService.bookingItemForOwnerId(booking.getItem().getId(),
                                                                            booking.getItem().getOwner().getId());
        assertEquals(1, bookingsDto.size());
        assertEquals(booking.getId(), bookingsDto.get(0).getId());
        assertEquals(booking.getItem().getId(), bookingsDto.get(0).getItem().getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals(booking.getBooker().getId(), bookingsDto.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookingsDto.get(0).getStatus());
    }

    @Test
    void bookingItemForBookerId_listBookingDto() {
        when(bookingRepository.findAllByBookerIdAndItemIdAndStartBefore(anyInt(), anyInt(), any(LocalDateTime.class)))
                                                                        .thenReturn(List.of(booking));
        List<BookingDto> bookingsDto = bookingService.bookingItemForBookerId(booking.getBooker().getId(),
                                                                             booking.getItem().getId(),
                                                                             LocalDateTime.now());
        assertEquals(1, bookingsDto.size());
        assertEquals(booking.getId(), bookingsDto.get(0).getId());
        assertEquals(booking.getItem().getId(), bookingsDto.get(0).getItem().getId());
        assertEquals(booking.getStart(), bookingsDto.get(0).getStart());
        assertEquals(booking.getEnd(), bookingsDto.get(0).getEnd());
        assertEquals(booking.getBooker().getId(), bookingsDto.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookingsDto.get(0).getStatus());
    }
}