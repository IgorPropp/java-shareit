package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingService {
    BookingDto create(Long userId, Booking booking) throws IllegalAccessException;

    BookingDto book(Long userId, Long bookingId, Boolean approved);

    BookingDto get(Long bookingId, Long userId) throws IllegalAccessException;
}
