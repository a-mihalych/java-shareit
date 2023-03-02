package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemNewDtoTest {

    @Autowired
    private JacksonTester<ItemNewDto> json;

    @Test
    void itemNewDtoJson() throws IOException {
        ItemNewDto itemNewDto = ItemNewDto.builder()
                                          .name("nameItem")
                                          .description("descriptionItem")
                                          .available(true)
                                          .requestId(1)
                                          .build();
        JsonContent<ItemNewDto> result = json.write(itemNewDto);
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemNewDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemNewDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemNewDto.getRequestId());
    }
}