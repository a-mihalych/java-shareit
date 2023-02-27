package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentNewDtoTest {

    @Autowired
    private JacksonTester<CommentNewDto> json;

    @Test
    @SneakyThrows
    void commentNewDtoJson() {
        CommentNewDto commentNewDto = CommentNewDto.builder()
                                                   .text("textComment")
                                                   .build();
        JsonContent<CommentNewDto> result = json.write(commentNewDto);
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentNewDto.getText());
    }
}
