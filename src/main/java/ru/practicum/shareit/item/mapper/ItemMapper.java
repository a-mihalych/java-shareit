package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemNewDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                      .id(item.getId())
                      .name(item.getName())
                      .description(item.getDescription())
                      .available(item.getAvailable())
                      .build();
    }

    public static Item toItem(ItemDto itemDto, Item item) {
        return Item.builder()
                   .id(item.getId())
                   .name(itemDto.getName() != null ? itemDto.getName() : item.getName())
                   .description(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription())
                   .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable())
                   .owner(item.getOwner())
                   .build();
    }

    public static Item toItem(ItemNewDto itemNewDto) {
        return Item.builder()
                   .name(itemNewDto.getName())
                   .description(itemNewDto.getDescription())
                   .available(itemNewDto.getAvailable())
                   .build();
    }
}
