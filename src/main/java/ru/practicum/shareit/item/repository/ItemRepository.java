package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemRepository {

    Map<Integer, Item> itemsForUserId(Integer userId);

    Item itemById(Integer itemId);

    List<Item> searchItem(String text);

    Item addItem(Item item);

    Item updateItem(Integer userId, Item item);
}
