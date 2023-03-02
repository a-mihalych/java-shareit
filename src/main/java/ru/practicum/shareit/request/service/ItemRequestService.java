package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(ItemRequestNewDto itemRequestNewDto, Integer userId);

    List<ItemRequestDto> itemsRequest(Integer userId);

    ItemRequestDto itemsRequestById(Integer userId, Integer itemRequestId);

    List<ItemRequestDto> itemsRequestAll(Integer userId, Integer from, Integer size);
}
