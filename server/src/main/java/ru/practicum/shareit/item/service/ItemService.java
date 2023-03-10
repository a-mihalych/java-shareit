package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemNewDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> itemsForId(Integer userId, Integer from, Integer size);

    ItemDto itemById(Integer userId, Integer itemId);

    List<ItemDto> searchItem(Integer userId, String text, Integer from, Integer size);

    ItemDto addItem(Integer userId, ItemNewDto itemNewDto, ItemRequestDto requestDto);

    ItemDto updateItem(Integer userId, ItemDto itemDto, Integer itemId);

    CommentDto addComment(Integer userId, Integer itemId, CommentNewDto commentNewDto);

    List<ItemDto> itemByRequestId(Integer requestId);
}
