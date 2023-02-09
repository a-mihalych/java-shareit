package ru.practicum.shareit.error.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    private String error;

    public ErrorResponse(String err) {
        this.error = err;
    }
}
