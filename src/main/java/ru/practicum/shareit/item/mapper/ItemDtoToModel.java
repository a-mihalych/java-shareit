package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemNewDto;
import ru.practicum.shareit.item.model.Item;

public class ItemDtoToModel {

    public static Item itemDtoToItem(ItemDto itemDto, Item item) {
        return Item.builder()
                   .id(item.getId())
                   .name(itemDto.getName() != null ? itemDto.getName() : item.getName())
                   .description(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription())
                   .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable())
                   .build();
    }

    public static Item itemNewDtoToItem(ItemNewDto itemNewDto) {
        return Item.builder()
                   .name(itemNewDto.getName())
                   .description(itemNewDto.getDescription())
                   .available(itemNewDto.getAvailable())
                   .build();
    }
}
