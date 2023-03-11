package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.ShareItApp.USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestService requestService;

    @GetMapping
    public List<ItemRequestDto> itemsRequest(@RequestHeader(USER_ID) Integer userId) {
        log.info("* Запрос Get: получение своих запросов пользователем с id = {}", userId);
        return requestService.itemsRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> itemsRequestAll(@RequestHeader(USER_ID) Integer userId,
                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("* Запрос Get: получение запросов других пользователей пользователем с id = {}, from = {}, size = {}",
                 userId, from, size);
        return requestService.itemsRequestAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto itemsRequestById(@RequestHeader(USER_ID) Integer userId,
                                           @PathVariable Integer requestId) {
        log.info("* Запрос Get: получение запроса с id = {}, пользователем с id = {}", requestId, userId);
        return requestService.itemsRequestById(userId, requestId);
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(USER_ID) Integer userId,
                                            @RequestBody ItemRequestNewDto itemRequestNewDto) {
        log.info("* Запрос Post: добавление нового запроса на вещь {}, пользователем с id = {}",
                 itemRequestNewDto, userId);
        return requestService.createItemRequest(itemRequestNewDto, userId);
    }
}
