package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String SHARERUSERID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(name = SHARERUSERID) Long userId,
                             @RequestBody @Valid BookingDtoRequest booking) throws IllegalAccessException {
        return bookingService.create(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto book(@RequestHeader(name = SHARERUSERID) Long userId,
                           @PathVariable Long bookingId,
                           @RequestParam(value = "approved") Boolean approved) throws IllegalAccessException {
        return bookingService.book(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@PathVariable Long bookingId,
                          @RequestHeader(name = SHARERUSERID) Long userId) throws IllegalAccessException {
        return bookingService.get(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingForOwner(@RequestHeader(name = SHARERUSERID) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(name = "from", defaultValue = "0")
                                                      @PositiveOrZero Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10")
                                                      @Positive Integer size) {
        return bookingService.getAllBookingsByOwner(userId, state, from/size, size);
    }

    @GetMapping
    public List<BookingDto> getAllForUserByState(@RequestHeader(name = SHARERUSERID) Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(name = "from", defaultValue = "0")
                                                     @PositiveOrZero Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10")
                                                     @Positive Integer size) {
        return bookingService.getAllBookingsForUserByState(userId, state, from/size, size);
    }
}
