package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestNewDto itemRequestNewDto, LocalDateTime now, User user) {
        return ItemRequest.builder()
                          .description(itemRequestNewDto.getDescription())
                          .created(now)
                          .requestor(user)
                          .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .requestor(user)
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                             .id(itemRequest.getId())
                             .description(itemRequest.getDescription())
                             .created(itemRequest.getCreated())
                             .items(new ArrayList<>())
                             .build();
    }
}
