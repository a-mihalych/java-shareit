package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserNewDtoTest {

    @Autowired
    private JacksonTester<UserNewDto> json;

    @Test
    void userNewDtoJson() throws IOException {
        UserNewDto userNewDto = UserNewDto.builder()
                .email("user@mail.com")
                .name("UserName")
                .build();
        User user = UserMapper.toUser(userNewDto);
        JsonContent<UserNewDto> result = json.write(userNewDto);
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userNewDto.getEmail());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userNewDto.getName());
    }
}