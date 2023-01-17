package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequestDto itemRequestDto;
}
