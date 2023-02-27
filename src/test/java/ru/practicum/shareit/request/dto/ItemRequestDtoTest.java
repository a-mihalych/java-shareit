package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    @SneakyThrows
    void itemRequestDtoJson() {
        ItemRequest request = ItemRequest.builder()
                                         .id(1)
                                         .description("descriptionItemRequest")
                                         .created(LocalDateTime.now())
                                         .requestor(new User())
                                         .build();
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);
        JsonContent<ItemRequestDto> result = json.write(requestDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(requestDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.description")
                          .isEqualTo(requestDto.getDescription());
        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }
}