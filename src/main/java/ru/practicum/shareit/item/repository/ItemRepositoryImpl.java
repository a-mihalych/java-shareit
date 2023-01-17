package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private Integer id = 1;
    private Map<Integer, Map<Integer, Item>> items = new HashMap<>();

    @Override
    public Map<Integer, Item> itemsForUserId(Integer userId) {
        return items.get(userId);
    }

    @Override
    public Item itemById(Integer itemId) {
        for (Map<Integer, Item> value : items.values()) {
            if (value.containsKey(itemId)) {
                return value.get(itemId);
            }
        }
        return null;
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> itemsSearchByText = new ArrayList<>();
        for (Map<Integer, Item> value : items.values()) {
            for (Item item : value.values()) {
                if ((item.getAvailable()) &&
                    (item.getName().toLowerCase().contains(text) ||
                     item.getDescription().toLowerCase().contains(text))) {
                    itemsSearchByText.add(item);
                }
            }
        }
        return itemsSearchByText;
    }

    @Override
    public Item addItem(Item item) {
        item.setId(nextId());
        Integer userId = item.getOwner().getId();
        if (!items.containsKey(userId)) {
            items.put(userId, new HashMap<>());
        }
        items.get(userId).put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Integer userId, Item item) {
        items.get(userId).put(item.getId(), item);
        return item;
    }

    private Integer nextId() {
        return id++;
    }
}
