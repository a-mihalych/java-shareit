package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserNewDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> usersAll() {
        log.info("* Запрос Get: получение списка пользователей");
        return userClient.usersAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> userById(@PathVariable Integer userId) {
        log.info("* Запрос Get: получение пользователя по id = {}", userId);
        return userClient.userById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserNewDto userNewDto) {
        log.info("* Запрос Post: создание пользователя {}", userNewDto);
        return userClient.createUser(userNewDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Integer userId, @RequestBody UserDto userDto) {
        log.info("* Запрос Patch: обновление пользователя с  id = {}, пользователь: {}", userId, userDto);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        log.info("* Запрос Delete: удаление пользователя по id = {}", userId);
        userClient.deleteUser(userId);
    }
}
