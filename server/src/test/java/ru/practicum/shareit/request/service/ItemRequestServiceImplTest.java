package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemServiceImpl itemService;
    @InjectMocks
    private ItemRequestServiceImpl requestService;
    private ItemRequest request;
    private Integer userId;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        request = ItemRequest.builder()
                             .id(1)
                             .description("descriptionItemRequest")
                             .requestor(new User())
                             .created(LocalDateTime.now())
                             .build();
        userId = 1;
        userDto = UserDto.builder()
                         .id(userId)
                         .email("user@mail.com")
                         .name("UserName")
                         .build();
    }

    @Test
    void createItemRequest_itemRequestDto() {
        ItemRequestNewDto requestNewDto = ItemRequestNewDto.builder()
                                                           .description("descriptionRequestNewDto")
                                                           .build();
        when(userService.userById(userId)).thenReturn(userDto);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);
        ItemRequestDto requestDto = requestService.createItemRequest(requestNewDto, userId);
        assertEquals(request.getId(), requestDto.getId());
        assertEquals(request.getDescription(), requestDto.getDescription());
        assertEquals(request.getCreated(), requestDto.getCreated());
        assertEquals(0, requestDto.getItems().size());
    }

    @Test
    void itemsRequest_listItemRequestDto() {
        when(userService.userById(userId)).thenReturn(userDto);
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(anyInt())).thenReturn(List.of(request));
        when(itemService.itemByRequestId(anyInt())).thenReturn(List.of());
        List<ItemRequestDto> requestsDto = requestService.itemsRequest(userId);
        assertEquals(1, requestsDto.size());
        assertEquals(request.getId(), requestsDto.get(0).getId());
        assertEquals(request.getDescription(), requestsDto.get(0).getDescription());
        assertEquals(request.getCreated(), requestsDto.get(0).getCreated());
        assertEquals(0, requestsDto.get(0).getItems().size());
    }

    @Test
    void itemsRequestById_itemRequestDto() {
        Integer itemRequestId = 1;
        when(userService.userById(userId)).thenReturn(userDto);
        when(requestRepository.findById(itemRequestId)).thenReturn(Optional.of(request));
        when(itemService.itemByRequestId(request.getId())).thenReturn(List.of());
        ItemRequestDto requestDto = requestService.itemsRequestById(userId, itemRequestId);
        assertEquals(request.getId(), requestDto.getId());
        assertEquals(request.getDescription(), requestDto.getDescription());
        assertEquals(request.getCreated(), requestDto.getCreated());
        assertEquals(0, requestDto.getItems().size());
    }

    @Test
    void itemsRequestById_notFoundException() {
        Integer itemRequestId = 1;
        when(userService.userById(userId)).thenReturn(userDto);
        when(requestRepository.findById(itemRequestId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.itemsRequestById(userId, itemRequestId));
    }

    @Test
    void itemsRequestAll_listItemRequestDto() {
        Integer from = 0;
        Integer size = 10;
        PageImpl<ItemRequest> page = new PageImpl<>(List.of(request));
        when(requestRepository.itemsRequestAllForUser(userId, PageRequest.ofSize(10))).thenReturn(page);
        when(itemService.itemByRequestId(anyInt())).thenReturn(List.of());
        List<ItemRequestDto> requestsDto = requestService.itemsRequestAll(userId, from, size);
        assertEquals(1, requestsDto.size());
        assertEquals(request.getId(), requestsDto.get(0).getId());
        assertEquals(request.getDescription(), requestsDto.get(0).getDescription());
        assertEquals(request.getCreated(), requestsDto.get(0).getCreated());
        assertEquals(0, requestsDto.get(0).getItems().size());
    }
}