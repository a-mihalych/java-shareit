package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserNewDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> usersAll();

    UserDto userById(Integer userId);

    UserDto createUser(UserNewDto userNewDto);

    UserDto updateUser(Integer userId, UserDto userDto);

    void deleteUser(Integer userId);
}
