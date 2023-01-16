package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserValidation {

    private final UserRepository userRepository;

    public boolean uniqueEmailValidation(String email) {
        if (userRepository.usersAll()
                          .stream()
                          .anyMatch(c -> c.getEmail().equals(email))) {
            throw new ConflictException("Email уже существует.");
        }
        return true;
    }

    public User notFoundUserValidation(Integer userId) {
        if (userId == null) {
            throw new NotFoundException("Не задан id пользователя");
        }
        User user = userRepository.userById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", userId));
        }
        return user;
    }
}
