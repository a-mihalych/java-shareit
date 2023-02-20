package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemNewDto {

    @NotBlank(message = "Название не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание не должно быть пустым")
    @Size(max = 256, message = "Длина описания ограничена 256 символами")
    private String description;
    @NotNull(message = "Статус для аренды должен быть задан")
    private Boolean available;
    private Integer requestId;
}
