package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserNewDto {

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email должен быть задан правильно")
    private String email;
}
