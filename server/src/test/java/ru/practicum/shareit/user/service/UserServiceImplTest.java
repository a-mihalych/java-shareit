package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserNewDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                   .id(1)
                   .email("user@mail.com")
                   .name("UserName")
                   .build();
    }

    @Test
    void usersAll_listUserDto() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> usersDto = userService.usersAll();
        assertEquals(1, usersDto.size());
        assertEquals(user.getId(), usersDto.get(0).getId());
        assertEquals(user.getEmail(), usersDto.get(0).getEmail());
        assertEquals(user.getName(), usersDto.get(0).getName());
    }

    @Test
    void usersAll_listEmpty() {
        when(userRepository.findAll()).thenReturn(List.of());
        List<UserDto> usersDto = userService.usersAll();
        assertEquals(0, usersDto.size());
    }

    @Test
    void userById_userDto() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        UserDto userDto = userService.userById(user.getId());
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    void userById_notFoundException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.userById(anyInt()));
    }

    @Test
    void createUser_userDto() {
        UserNewDto userNewDto = UserNewDto.builder()
                                          .email("user@mail.com")
                                          .name("UserName")
                                          .build();
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto userDto = userService.createUser(userNewDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    void updateUser_userDto() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto userDto = userService.updateUser(user.getId(), UserMapper.toUserDto(user));
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    void updateUser_notFoundException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.userById(anyInt()));
    }

    @Test
    void deleteUser_void() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        userService.deleteUser(user.getId());
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_notFoundException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.userById(anyInt()));
    }
}