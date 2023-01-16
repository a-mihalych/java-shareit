package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemNewDto;
import ru.practicum.shareit.item.mapper.ItemDtoToModel;
import ru.practicum.shareit.item.mapper.ItemModelToDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserValidation userValidation;
    private final ItemValidation itemValidation;

    @Override
    public List<ItemDto> itemsForId(Integer userId) {
        List<Item> items = new ArrayList<>(itemRepository.itemsForUserId(userId).values());
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        return items.stream()
                    .map(ItemModelToDto::itemToItemDto)
                    .collect(Collectors.toList());
    }

    @Override
    public ItemDto itemById(Integer userId, Integer itemId) {
        Item item = itemRepository.itemById(itemId);
        if (item == null) {
            throw new NotFoundException(String.format("Не найдена вещь с id = %d", itemId));
        }
        return ItemModelToDto.itemToItemDto(item);
    }

    @Override
    public List<ItemDto> searchItem(Integer userId, String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.searchItem(text.toLowerCase());
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        return items.stream()
                    .map(ItemModelToDto::itemToItemDto)
                    .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(Integer userId, ItemNewDto itemNewDto) {
        userValidation.notFoundUserValidation(userId);
        Item item = ItemDtoToModel.itemNewDtoToItem(itemNewDto);
        return ItemModelToDto.itemToItemDto(itemRepository.addItem(userId, item));
    }

    @Override
    public ItemDto updateItem(Integer userId, ItemDto itemDto, Integer itemId) {
        userValidation.notFoundUserValidation(userId);
        Item item = itemValidation.notFoundItemValidation(userId, itemId);
        item = ItemDtoToModel.itemDtoToItem(itemDto, item);
        return ItemModelToDto.itemToItemDto(itemRepository.updateItem(userId, item));
    }
}
