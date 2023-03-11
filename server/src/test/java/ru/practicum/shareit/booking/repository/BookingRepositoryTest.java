package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private ItemRepository itemRepository;
    private Item item;
    private User user1;
    private User user2;
    private User user3;
    private ItemRequest request;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                .email("user1@mail.com")
                .name("User1Name")
                .build();
        user2 = User.builder()
                .email("user2@mail.com")
                .name("User2Name")
                .build();
        user3 = User.builder()
                .email("user3@mail.com")
                .name("User3Name")
                .build();
        request = ItemRequest.builder()
                .description("ItemRequestDescription")
                .created(LocalDateTime.now())
                .requestor(user1)
                .build();
        item = Item.builder()
                .name("nameItem")
                .description("descriptionItem")
                .available(true)
                .owner(user2)
                .itemRequest(request)
                .build();
        booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(user3)
                .status(StatusBooking.ALL)
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        requestRepository.save(request);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        requestRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                                       user3.getId(), start, end, pageRequest);
        List<Booking> bookings = bookingsPage.getContent();
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findAllCurrentForItemsOwner() {
        LocalDateTime date = LocalDateTime.now().plusDays(3);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = bookingRepository.findAllCurrentForItemsOwner(user2.getId(), date, pageRequest);
        List<Booking> bookings = bookingsPage.getContent();
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByStartDesc() {
        LocalDateTime date = LocalDateTime.now().plusDays(5);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                                                       user3.getId(), date, pageRequest);
        List<Booking> bookings = bookingsPage.getContent();
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findAllPastForItemsOwner() {
        LocalDateTime date = LocalDateTime.now().plusDays(5);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = bookingRepository.findAllPastForItemsOwner(user2.getId(), date, pageRequest);
        List<Booking> bookings = bookingsPage.getContent();
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                                                       user3.getId(), date, pageRequest);
        List<Booking> bookings = bookingsPage.getContent();
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findAllFutureForItemsOwner() {
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = bookingRepository.findAllFutureForItemsOwner(user2.getId(), date, pageRequest);
        List<Booking> bookings = bookingsPage.getContent();
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        StatusBooking status = StatusBooking.ALL;
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                                                       user3.getId(), status, pageRequest);
        List<Booking> bookings = bookingsPage.getContent();
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findAllForItemsOwnerByStatus() {
        StatusBooking status = StatusBooking.ALL;
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = bookingRepository.findAllForItemsOwnerByStatus(user2.getId(), status, pageRequest);
        List<Booking> bookings = bookingsPage.getContent();
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = bookingRepository.findAllByBookerIdOrderByStartDesc(user3.getId(), pageRequest);
        List<Booking> bookings = bookingsPage.getContent();
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findAllForItemsOwner() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = bookingRepository.findAllForItemsOwner(user2.getId(), pageRequest);
        List<Booking> bookings = bookingsPage.getContent();
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findAllByItemIdAndItemOwnerIdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndItemOwnerIdOrderByStartDesc(
                                                   item.getId(), user2.getId());
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findAllByBookerIdAndItemIdAndStartBefore() {
        LocalDateTime date = LocalDateTime.now().plusDays(3);
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndItemIdAndStartBefore(
                                                   user3.getId(), item.getId(), date);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertEquals(booking.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }
}
