package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingForList;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, Booking booking) throws IllegalAccessException;

    BookingDto book(Long userId, Long bookingId, Boolean approved) throws IllegalAccessException;

    BookingDto get(Long bookingId, Long userId) throws IllegalAccessException;

    List<BookingForList> getAllBookingsByOwner(Long userId, String string);

    List<BookingForList> getAllBookingsForUserByState(Long userId, String string);
}
