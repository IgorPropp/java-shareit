package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetAllBookingsDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.storage.GetAllBookingsStorage;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final GetAllBookingsStorage getAllBookingsStorage;
    private final ItemMapper itemMapper;

    public BookingDto create(Long userId, Booking booking) {
        if (!bookingDatesAreValid(booking)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking time is incorrect");
        }
        UserDto userDto = userService.getUser(userId);
        ItemDto itemDto = itemMapper.toDto(itemStorage.findById(booking.getItemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found")));
        if (itemStorage.getById(booking.getItemId()).getOwner().getId().equals(userId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is the owner");
        if (itemDto.getAvailable().equals(false)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item is unavailable");
        }
        booking.setBooker(userDto.getId());
        booking.setStatus(BookingStatus.WAITING);
        bookingStorage.save(booking);
        return BookingMapper.toDto(booking, itemDto, userDto);
    }

    public BookingDto book(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        Item item = itemStorage.findById(booking.getItemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        User booker = userStorage.findById(booking.getBooker()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booker not found"));
        if (item.getOwner().getId().equals(userId)) {
            if (approved && booking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking already approved");
            }
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            bookingStorage.save(booking);
            return BookingMapper.toDto(booking, itemMapper.toDto(item), UserMapper.toDto(booker));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found");
    }

    public BookingDto get(Long bookingId, Long userId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        Item item = itemStorage.findById(booking.getItemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        User user = userStorage.findById(booking.getBooker()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (booking.getBooker().equals(userId) || item.getOwner().getId().equals(userId)) {
            return BookingMapper.toDto(booking, itemMapper.toDto(item), UserMapper.toDto(user));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found");
    }

    public List<GetAllBookingsDto> getAllBookingsByOwner(Long userId, String string) {
        try {
            BookingState state = BookingState.valueOf(string);
            User user = userStorage.findById(userId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            List<Long> userItemsIds = itemStorage.findAllByOwner(user)
                    .stream()
                    .map(Item::getId)
                    .collect(Collectors.toList());
            List<GetAllBookingsDto> bookingsByOwner;
            if (!userItemsIds.isEmpty()) {
                switch (state) {
                    case ALL:
                        bookingsByOwner = getAllBookingsStorage.getAllForOwner(userItemsIds);
                        break;
                    case CURRENT:
                        bookingsByOwner = getAllBookingsStorage.getCurrentBookingsForOwner(userItemsIds);
                        break;
                    case PAST:
                        bookingsByOwner = getAllBookingsStorage.getPastBookingsForOwner(userItemsIds);
                        break;
                    case FUTURE:
                        bookingsByOwner = getAllBookingsStorage.getFutureBookingsForOwner(userItemsIds);
                        break;
                    case WAITING:
                        bookingsByOwner = getAllBookingsStorage.findBookingByOwnerAndStatusOrderByEndDesc(userItemsIds, BookingStatus.WAITING);
                        break;
                    case REJECTED:
                        bookingsByOwner = getAllBookingsStorage.findBookingByOwnerAndStatusOrderByEndDesc(userItemsIds, BookingStatus.REJECTED);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            } else {
                throw new IllegalStateException("User has no items");
            }
            return bookingsByOwner;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    public List<GetAllBookingsDto> getAllBookingsForUserByState(Long userId, String string) {
        try {
            BookingState state = BookingState.valueOf(string);
            Long userIdValue = userService.getUser(userId).getId();
            List<GetAllBookingsDto> bookings = new ArrayList<>();
            switch (state) {
                case ALL:
                    bookings = getAllBookingsStorage.findAllByBookerOrderByEndDesc(userIdValue);
                    break;
                case CURRENT:
                    bookings = getAllBookingsStorage.getCurrentBookingsForBooker(userIdValue);
                    break;
                case PAST:
                    bookings = getAllBookingsStorage.getPastBookingsForBooker(userIdValue);
                    break;
                case FUTURE:
                    bookings = getAllBookingsStorage.getFutureBookingsForBooker(userIdValue);
                    break;
                case WAITING:
                    bookings = getAllBookingsStorage.findBookingByBookerAndStatusOrderByEndDesc(userIdValue, BookingStatus.WAITING);
                    break;
                case REJECTED:
                    bookings = getAllBookingsStorage.findBookingByBookerAndStatusOrderByEndDesc(userIdValue, BookingStatus.REJECTED);
                    break;
            }
            return bookings;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private boolean bookingDatesAreValid(Booking booking) {
        return booking.getStart() != null && booking.getEnd() != null && !booking.getStart().isAfter(booking.getEnd()) &&
                !booking.getEnd().isBefore(booking.getStart()) && booking.getStart() != booking.getEnd() &&
                !booking.getStart().equals(booking.getEnd()) && !booking.getStart().isBefore(LocalDateTime.now());
    }

}
