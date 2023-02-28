package ru.practicum.shareit.error.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    String error;

    public ErrorResponse(String err) {
        this.error = err;
    }
}
