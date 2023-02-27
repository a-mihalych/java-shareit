package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private ItemService itemService;
    private BookingDto bookingDto;
    private Item item;
    private User user1;
    private User user2;
    private Integer userId;

    @BeforeEach
    void beforeEach() {
        userId = 1;
        user1 = User.builder()
                    .id(userId)
                    .name("User1Name")
                    .email("user1@mail.com")
                    .build();
        user2 = User.builder()
                    .id(2)
                    .name("User2Name")
                    .email("user2@mail.com")
                    .build();
        item = Item.builder()
                   .id(1)
                   .name("nameItem")
                   .description("descriptionItem")
                   .available(true)
                   .owner(user1)
                   .build();
        bookingDto = BookingDto.builder()
                               .id(1)
                               .item(item)
                               .booker(user2)
                               .build();
    }

    @Test
    @SneakyThrows
    void bookingForUserId() {
        when(bookingService.bookingForUserId(anyInt(), any(StatusBooking.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(bookingService).bookingForUserId(anyInt(), any(StatusBooking.class), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void bookingForUserId_shareitException() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ERR"))
                .andExpect(status().is(500));
        verify(bookingService, never()).bookingForUserId(anyInt(), any(StatusBooking.class), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void bookingAllItemForUserId() {
        when(bookingService.bookingAllItemForUserId(anyInt(), any(StatusBooking.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(bookingService).bookingAllItemForUserId(anyInt(), any(StatusBooking.class), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void bookingAllItemForUserId_shareitException() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ERR"))
                .andExpect(status().is(500));
        verify(bookingService, never()).bookingAllItemForUserId(anyInt(), any(StatusBooking.class), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void bookingById() {
        Integer bookingId = bookingDto.getId();
        when(bookingService.bookingById(anyInt(), anyInt())).thenReturn(bookingDto);
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(bookingService).bookingById(anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void addBooking() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        BookingNewDto bookingNewDto = BookingNewDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        when(itemService.itemById(anyInt(), anyInt())).thenReturn(itemDto);
        when(bookingService.addBooking(anyInt(), any(BookingNewDto.class), any(ItemDto.class))).thenReturn(bookingDto);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingNewDto)))
                .andExpect(status().isOk());
        verify(bookingService).addBooking(anyInt(), any(BookingNewDto.class), any(ItemDto.class));
    }

    @Test
    @SneakyThrows
    void updateBooking() {
        Integer bookingId = bookingDto.getId();
        when(bookingService.updateBooking(anyInt(), anyInt(), any(StatusBooking.class))).thenReturn(bookingDto);
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .param("approved", "true"))
               .andExpect(status().isOk());
        verify(bookingService).updateBooking(anyInt(), anyInt(), any(StatusBooking.class));
    }
}
