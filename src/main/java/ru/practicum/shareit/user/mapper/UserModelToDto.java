package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserModelToDto {

    public static UserDto userToUserDto(User user) {
        return UserDto.builder()
                      .id(user.getId())
                      .name(user.getName())
                      .email(user.getEmail())
                      .build();
    }
}
