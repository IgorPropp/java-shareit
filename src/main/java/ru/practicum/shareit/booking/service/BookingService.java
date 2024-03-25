package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetAllBookingsDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, Booking booking) throws IllegalAccessException;

    BookingDto book(Long userId, Long bookingId, Boolean approved);

    BookingDto get(Long bookingId, Long userId) throws IllegalAccessException;

    List<GetAllBookingsDto> getAllBookingsByOwner(Long userId, String string);

    List<GetAllBookingsDto> getAllBookingsForUserByState(Long userId, String string);
}
