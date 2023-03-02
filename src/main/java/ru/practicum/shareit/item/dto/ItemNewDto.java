package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemNewDto {

    @NotBlank(message = "Название не должно быть пустым")
    String name;
    @NotBlank(message = "Описание не должно быть пустым")
    @Size(max = 256, message = "Длина описания ограничена 256 символами")
    String description;
    @NotNull(message = "Статус для аренды должен быть задан")
    Boolean available;
    Integer requestId;
}
