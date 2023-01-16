package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserNewDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoToModel;
import ru.practicum.shareit.user.mapper.UserModelToDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidation userValidation;

    @Override
    public List<UserDto> usersAll() {
        List<User> users = userRepository.usersAll();
        if (users.isEmpty()) {
            return new ArrayList<>();
        }
        return users.stream()
                    .map(UserModelToDto::userToUserDto)
                    .collect(Collectors.toList());
    }

    @Override
    public UserDto userById(Integer userId) {
        User user = userRepository.userById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", userId));
        }
        return UserModelToDto.userToUserDto(user);
    }

    @Override
    public UserDto createUser(UserNewDto userNewDto) {
        userValidation.uniqueEmailValidation(userNewDto.getEmail());
        User user = UserDtoToModel.userNewDtoToUser(userNewDto);
        return UserModelToDto.userToUserDto(userRepository.createUser(user));
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) {
        User user = userValidation.notFoundUserValidation(userId);
        if (userDto.getEmail() != null) {
            userValidation.uniqueEmailValidation(userDto.getEmail());
        }
        user = UserDtoToModel.userDtoToUser(userDto, user);
        return UserModelToDto.userToUserDto(userRepository.updateUser(user));
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteUser(userId);
    }
}
