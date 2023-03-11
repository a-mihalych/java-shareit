package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.ShareItGateway.USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> itemsRequest(@RequestHeader(USER_ID) Integer userId) {
        log.info("* Запрос Get: получение своих запросов пользователем с id = {}", userId);
        return requestClient.itemsRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> itemsRequestAll(@RequestHeader(USER_ID) Integer userId,
                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("* Запрос Get: получение запросов других пользователей пользователем с id = {}, from = {}, size = {}",
                 userId, from, size);
        return requestClient.itemsRequestAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> itemsRequestById(@RequestHeader(USER_ID) Integer userId,
                                           @PathVariable Integer requestId) {
        log.info("* Запрос Get: получение запроса с id = {}, пользователем с id = {}", requestId, userId);
        return requestClient.itemsRequestById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(USER_ID) Integer userId,
                                            @Valid @RequestBody ItemRequestNewDto itemRequestNewDto) {
        log.info("* Запрос Post: добавление нового запроса на вещь {}, пользователем с id = {}",
                 itemRequestNewDto, userId);
        return requestClient.createItemRequest(itemRequestNewDto, userId);
    }
}
