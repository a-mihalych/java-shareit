package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemNewDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.ShareItApp.USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final ItemRequestService requestService;

    @GetMapping
    public List<ItemDto> itemsForId(@RequestHeader(USER_ID) Integer userId,
                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("* Запрос Get: получение списка вещей по id пользователя, id = {}", userId);
        return itemService.itemsForId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto itemById(@RequestHeader(USER_ID) Integer userId, @PathVariable Integer itemId) {
        log.info("* Запрос Get: получение вещи по id = {}, пользователем с id = {}", itemId, userId);
        return itemService.itemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(USER_ID) Integer userId,
                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                    @RequestParam String text) {
        log.info("* Запрос Get: получение списка вещей, поиск строки '{}', пользователем с id = {}", text, userId);
        return itemService.searchItem(userId, text, from, size);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(USER_ID) Integer userId,
                           @Valid @RequestBody ItemNewDto itemNewDto) {
        log.info("* Запрос Post: добавление новой вещи {} пользователем с id = {}", itemNewDto, userId);
        ItemRequestDto requestDto = null;
        if (itemNewDto.getRequestId() != null) {
            requestDto = requestService.itemsRequestById(userId, itemNewDto.getRequestId());
        }
        return itemService.addItem(userId, itemNewDto, requestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID) Integer userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Integer itemId) {
        log.info("* Запрос Patch: обновление вещи {}, её id = {}, пользователь с id = {}", itemDto, itemId, userId);
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID) Integer userId,
                                 @Valid @RequestBody CommentNewDto commentNewDto,
                                 @PathVariable Integer itemId) {
        log.info("* Запрос Post: добавление нового коментария {}, пользователем с id = {}, для вещи с id = {}",
                 commentNewDto, userId, itemId);
        return itemService.addComment(userId, itemId, commentNewDto);
    }
}
