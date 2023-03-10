package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemNewDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.List;

import static ru.practicum.shareit.ShareItGateway.USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> itemsForId(@RequestHeader(USER_ID) Integer userId,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("* Запрос Get: получение списка вещей по id пользователя, id = {}", userId);
        return itemClient.itemsForId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> itemById(@RequestHeader(USER_ID) Integer userId, @PathVariable Integer itemId) {
        log.info("* Запрос Get: получение вещи по id = {}, пользователем с id = {}", itemId, userId);
        return itemClient.itemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(USER_ID) Integer userId,
                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                    @RequestParam String text) {
        log.info("* Запрос Get: получение списка вещей, поиск строки '{}', пользователем с id = {}", text, userId);
        if (text.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID) Integer userId,
                           @Valid @RequestBody ItemNewDto itemNewDto) {
        log.info("* Запрос Post: добавление новой вещи {} пользователем с id = {}", itemNewDto, userId);
        return itemClient.addItem(userId, itemNewDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID) Integer userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Integer itemId) {
        log.info("* Запрос Patch: обновление вещи {}, её id = {}, пользователь с id = {}", itemDto, itemId, userId);
        return itemClient.updateItem(userId, itemDto, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID) Integer userId,
                                 @Valid @RequestBody CommentNewDto commentNewDto,
                                 @PathVariable Integer itemId) {
        log.info("* Запрос Post: добавление нового коментария {}, пользователем с id = {}, для вещи с id = {}",
                 commentNewDto, userId, itemId);
        return itemClient.addComment(userId, itemId, commentNewDto);
    }
}
