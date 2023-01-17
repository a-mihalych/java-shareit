package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemNewDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
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
        return items.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
    }

    @Override
    public ItemDto itemById(Integer userId, Integer itemId) {
        Item item = itemRepository.itemById(itemId);
        if (item == null) {
            throw new NotFoundException(String.format("Не найдена вещь с id = %d", itemId));
        }
        return ItemMapper.toItemDto(item);
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
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(Integer userId, ItemNewDto itemNewDto) {
        User user = userValidation.notFoundUserValidation(userId);
        Item item = ItemMapper.toItem(itemNewDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.addItem(item));
    }

    @Override
    public ItemDto updateItem(Integer userId, ItemDto itemDto, Integer itemId) {
        userValidation.notFoundUserValidation(userId);
        Item item = itemValidation.notFoundItemValidation(userId, itemId);
        if ((itemDto.getId() != null) && !(itemDto.getId().equals(itemId))) {
            throw new ConflictException(String.format("Обноновление прервано, разные id, %d не равно %d",
                                                      itemDto.getId(), itemId));
        }
        if (!item.getOwner().getId().equals(userId)) {
            return itemDto;
        }
        item = ItemMapper.toItem(itemDto, item);
        return ItemMapper.toItemDto(itemRepository.updateItem(userId, item));
    }
}
