package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Integer id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
