package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingDtoRequest booking) throws IllegalAccessException;

    BookingDto book(Long userId, Long bookingId, Boolean approved) throws IllegalAccessException;

    BookingDto get(Long bookingId, Long userId) throws IllegalAccessException;

    List<BookingDto> getAllBookingsByOwner(Long userId, String string);

    List<BookingDto> getAllBookingsForUserByState(Long userId, String string);
}
