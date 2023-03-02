package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService requestService;
    private ItemRequestDto requestDto;
    private Integer userId;

    @BeforeEach
    void beforeEach() {
        requestDto = ItemRequestDto.builder()
                                   .id(1)
                                   .description("descriptionItemRequestDto")
                                   .created(LocalDateTime.now())
                                   .items(List.of())
                                   .build();
        userId = 1;
    }

    @Test
    void itemsRequest() throws Exception {
        when(requestService.itemsRequest(anyInt())).thenReturn(List.of(requestDto));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(List.of(requestDto))));
        verify(requestService).itemsRequest(anyInt());
    }

    @Test
    void itemsRequestAll() throws Exception {
        when(requestService.itemsRequestAll(anyInt(), anyInt(), anyInt())).thenReturn(List.of(requestDto));
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(requestDto))));
        verify(requestService).itemsRequestAll(anyInt(), anyInt(), anyInt());
    }

    @Test
    void itemsRequestById() throws Exception {
        Integer requestId = 1;
        when(requestService.itemsRequestById(anyInt(), anyInt())).thenReturn(requestDto);
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));
        verify(requestService).itemsRequestById(anyInt(), anyInt());
    }

    @Test
    void createItemRequest() throws Exception {
        ItemRequestNewDto requestNewDto = ItemRequestNewDto.builder()
                                                           .description("descriptionRequestNewDto")
                                                           .build();
        when(requestService.createItemRequest(any(ItemRequestNewDto.class), anyInt())).thenReturn(requestDto);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestNewDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));
        verify(requestService).createItemRequest(any(ItemRequestNewDto.class), anyInt());
    }
}