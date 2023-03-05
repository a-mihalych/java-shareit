package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserNewDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> usersAll() {
        log.info("* Запрос Get: получение списка пользователей");
        return userService.usersAll();
    }

    @GetMapping("/{userId}")
    public UserDto userById(@PathVariable Integer userId) {
        log.info("* Запрос Get: получение пользователя по id = {}", userId);
        return userService.userById(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserNewDto userNewDto) {
        log.info("* Запрос Post: создание пользователя {}", userNewDto);
        return userService.createUser(userNewDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Integer userId, @RequestBody UserDto userDto) {
        log.info("* Запрос Patch: обновление пользователя с  id = {}, пользователь: {}", userId, userDto);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        log.info("* Запрос Delete: удаление пользователя по id = {}", userId);
        userService.deleteUser(userId);
    }
}
