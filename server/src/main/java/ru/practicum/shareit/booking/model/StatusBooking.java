package ru.practicum.shareit.booking.model;

public enum StatusBooking {
    WAITING, APPROVED, REJECTED, CANCELED, CURRENT, PAST, FUTURE, ALL;

    public static StatusBooking from(String state) {
        for (StatusBooking value : StatusBooking.values()) {
            if (value.name().equals(state)) {
                return value;
            }
        }
        return null;
    }
}
