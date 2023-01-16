package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemNewDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> itemsForId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("* Запрос Get: получение списка вещей по id пользователя, id = {}", userId);
        return itemService.itemsForId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto itemById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
        log.info("* Запрос Get: получение вещи по id = {}, пользователем с id = {}", itemId, userId);
        return itemService.itemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam String text) {
        log.info("* Запрос Get: получение списка вещей, поиск строки '{}', пользователем с id = {}", text, userId);
        return itemService.searchItem(userId, text);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                           @Valid @RequestBody ItemNewDto itemNewDto) {
        log.info("* Запрос Post: добавление новой вещи {} пользователем с id = {}", itemNewDto, userId);
        return itemService.addItem(userId, itemNewDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                           @RequestBody ItemDto itemDto,
                           @PathVariable Integer itemId) {
        log.info("* Запрос Patch: обновление вещи {}, её id = {}, пользователь с id = {}", itemDto, itemId, userId);
        return itemService.updateItem(userId, itemDto, itemId);
    }
}
