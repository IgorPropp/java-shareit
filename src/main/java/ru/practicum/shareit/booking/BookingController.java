package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                             @RequestBody @Valid Booking booking) throws IllegalAccessException {
        return bookingService.create(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto book(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                           @PathVariable Long bookingId,
                           @RequestParam(value = "approved") Boolean approved) {
        return bookingService.book(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@PathVariable Long bookingId,
                          @RequestHeader(name = "X-Sharer-User-Id") long userId) throws IllegalAccessException {
        return bookingService.get(bookingId, userId);
    }
}
