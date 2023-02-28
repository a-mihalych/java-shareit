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
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void userDtoJson() throws IOException {
        User user = User.builder()
                        .id(1)
                        .email("user@mail.com")
                        .name("UserName")
                        .build();
        UserDto userDto = UserMapper.toUserDto(user);
        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
    }
}
