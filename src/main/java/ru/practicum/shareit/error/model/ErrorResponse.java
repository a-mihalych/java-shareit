package ru.practicum.shareit.error.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    private String err;

    public ErrorResponse(String err) {
        this.err = err;
    }
}
