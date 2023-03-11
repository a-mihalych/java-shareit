package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserNewDto {

    @NotBlank(message = "Имя не должно быть пустым")
    String name;
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email должен быть задан правильно")
    String email;
}
