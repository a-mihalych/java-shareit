package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserNewDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
        return users.stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
    }

    @Override
    public UserDto userById(Integer userId) {
        User user = userRepository.userById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", userId));
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserNewDto userNewDto) {
        userValidation.uniqueEmailValidation(userNewDto.getEmail());
        User user = UserMapper.toUser(userNewDto);
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) {
        User user = userValidation.notFoundUserValidation(userId);
        if (userDto.getEmail() != null) {
            userValidation.uniqueEmailValidation(userDto.getEmail());
        }
        user = UserMapper.toUser(userDto, user);
        return UserMapper.toUserDto(userRepository.updateUser(user));
    }

    @Override
    public void deleteUser(Integer userId) {
        userValidation.notFoundUserValidation(userId);
        userRepository.deleteUser(userId);
    }
}
