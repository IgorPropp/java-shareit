package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;
    private static final String SHARERUSERID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(SHARERUSERID) Long userId,
                                                @RequestBody @Valid Booking booking) {
        return bookingClient.createBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> book(@RequestHeader(SHARERUSERID) Long userId, @PathVariable Long bookingId,
                                       @RequestParam(value = "approved") Boolean approved) {
        return bookingClient.book(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Long bookingId, @RequestHeader(SHARERUSERID) Long userId) {
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsForOwner(@RequestHeader(SHARERUSERID) Long userId,
                                                         @RequestParam(name = "state", defaultValue = "ALL")
                                                         String state,
                                                         @RequestParam(name = "from", defaultValue = "0")
                                                             @PositiveOrZero Integer from,
                                                         @RequestParam(name = "size", defaultValue = "10")
                                                             @Positive Integer size) {
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getAllForUserByState(@RequestHeader(SHARERUSERID) Long userId,
                                                       @RequestParam(value = "state", defaultValue = "ALL")
                                                       String state,
                                                       @RequestParam(name = "from", defaultValue = "0")
                                                           @PositiveOrZero Integer from,
                                                       @RequestParam(name = "size", defaultValue = "10")
                                                           @Positive Integer size) {
        return bookingClient.getAllForUserByState(userId, state, from, size);
    }
}
