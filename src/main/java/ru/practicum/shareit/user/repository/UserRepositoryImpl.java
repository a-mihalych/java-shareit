package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private Integer id = 1;
    private Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> usersAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User userById(Integer userId) {
        return users.get(userId);
    }

    @Override
    public User createUser(User user) {
        user.setId(nextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Integer userId) {
        users.remove(userId);
    }

    private Integer nextId() {
        return id++;
    }
}
