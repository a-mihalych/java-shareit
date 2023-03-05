package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestNewDtoTest {

    @Autowired
    private JacksonTester<ItemRequestNewDto> json;

    @Test
    void itemRequestNewDtoJson() throws IOException {
        ItemRequestNewDto requestNewDto = ItemRequestNewDto.builder()
                .description("descriptionItemRequest")
                .build();
        JsonContent<ItemRequestNewDto> result = json.write(requestNewDto);
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description")
                          .isEqualTo(requestNewDto.getDescription());
    }
}