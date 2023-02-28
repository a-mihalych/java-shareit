package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserNewDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = UserDto.builder()
                         .id(1)
                         .email("user@mail.com")
                         .name("UserName")
                         .build();
    }

    @Test
    void usersAll() throws Exception {
        when(userService.usersAll()).thenReturn(List.of(userDto));
        mockMvc.perform(get("/users"))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(List.of(userDto))));
        verify(userService).usersAll();
    }

    @Test
    void userById() throws Exception {
        Integer userId = 1;
        when(userService.userById(userId)).thenReturn(userDto);
        mockMvc.perform(get("/users/{userId}", userId))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
        verify(userService).userById(userId);
    }

    @Test
    void createUser() throws Exception {
        UserNewDto userNewDto = UserNewDto.builder()
                .email("user@mail.com")
                .name("UserName")
                .build();
        when(userService.createUser(any(UserNewDto.class))).thenReturn(userDto);
        mockMvc.perform(post("/users")
                             .contentType(MediaType.APPLICATION_JSON)
                             .content(objectMapper.writeValueAsString(userNewDto)))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
        verify(userService).createUser(any(UserNewDto.class));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(anyInt(), any(UserDto.class))).thenReturn(userDto);
        Integer userId = 1;
        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
        verify(userService).updateUser(anyInt(), any(UserDto.class));
    }

    @Test
    void deleteUser() throws Exception {
        Integer userId = 1;
        mockMvc.perform(delete("/users/{userId}", userId))
               .andExpect(status().isOk());
        verify(userService).deleteUser(anyInt());
    }
}