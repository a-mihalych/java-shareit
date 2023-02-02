package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemValidation {

    private final ItemRepository itemRepository;

    public Item notFoundItemValidation(Integer userId, Integer itemId) {
        if (itemId == null) {
            throw new NotFoundException("Не задан id вещи");
        }
        Map<Integer, Item> items = itemRepository.itemsForUserId(userId);
        Item item = null;
        if (items != null) {
            item = items.get(itemId);
        }
        if (item == null) {
            throw new NotFoundException(String.format("Не найдена вещь с id = %d", itemId));
        }
        return item;
    }
}
