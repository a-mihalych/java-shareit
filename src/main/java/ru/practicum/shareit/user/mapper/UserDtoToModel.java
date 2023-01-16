package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserNewDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserDtoToModel {

    public static User userDtoToUser(UserDto userDto, User user) {
        return User.builder()
                   .id(user.getId())
                   .name(userDto.getName() != null ? userDto.getName() : user.getName())
                   .email(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail())
                   .build();
    }

    public static User userNewDtoToUser(UserNewDto userNewDto) {
        return User.builder()
                   .name(userNewDto.getName())
                   .email(userNewDto.getEmail())
                   .build();
    }
}
