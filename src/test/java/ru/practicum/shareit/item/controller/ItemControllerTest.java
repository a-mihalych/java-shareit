package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemNewDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService requestService;
    @MockBean
    private ItemService itemService;
    private ItemDto itemDto;
    private Integer userId;

    @BeforeEach
    void beforeEach() {
        itemDto = ItemDto.builder()
                .id(1)
                .name("nameItemDto")
                .available(true)
                .description("descriptionItemDto")
                .build();
        userId = 1;
    }

    @Test
    @SneakyThrows
    void itemsForId() {
        when(itemService.itemsForId(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemDto));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(itemService).itemsForId(anyInt(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void itemById() {
        Integer itemId = itemDto.getId();
        when(itemService.itemById(anyInt(), anyInt())).thenReturn(itemDto);
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(itemService).itemById(anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void searchItem() {
        when(itemService.searchItem(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "searchItem"))
               .andExpect(status().isOk());
        verify(itemService).searchItem(anyInt(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void addItem() {
        ItemNewDto itemNewDto = ItemNewDto.builder()
                .name("nameItemNewDto")
                .available(true)
                .description("descriptionItemNewDto")
                .build();
        when(itemService.addItem(anyInt(), any(ItemNewDto.class), any(ItemRequestDto.class))).thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemNewDto)))
                .andExpect(status().isOk());
        verify(itemService).addItem(anyInt(), any(ItemNewDto.class), any());
    }

    @Test
    @SneakyThrows
    void updateItem() {
        Integer itemId = itemDto.getId();
        when(itemService.updateItem(anyInt(), any(ItemDto.class), anyInt())).thenReturn(itemDto);
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
        verify(itemService).updateItem(anyInt(), any(ItemDto.class), anyInt());
    }

    @Test
    @SneakyThrows
    void addComment() {
        Integer itemId = itemDto.getId();
        CommentNewDto commentNewDto = CommentNewDto.builder()
                                                   .text("textComment")
                                                   .build();
        CommentDto commentDto = CommentDto.builder()
                                          .id(1)
                                          .text("textComment")
                                          .build();
        when(itemService.addComment(anyInt(), anyInt(), any(CommentNewDto.class))).thenReturn(commentDto);
        mockMvc.perform(post("/items//{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentNewDto)))
                .andExpect(status().isOk());
        verify(itemService).addComment(anyInt(), anyInt(), any(CommentNewDto.class));
    }
}