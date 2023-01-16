package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    List<User> usersAll();

    User userById(Integer userId);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Integer userId);
}
