package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingForList;
import ru.practicum.shareit.booking.storage.BookingForListStorage;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final BookingForListStorage bookingForListStorage;

    public BookingDto create(Long userId, Booking booking) throws IllegalAccessException {
        if (!bookingDatesAreValid(booking)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking time is incorrect");
        }
        UserDto userDto = UserMapper.toDto(userStorage.findById(userId).orElseThrow());
        ItemDto itemDto = ItemMapper.toDto(itemStorage.findById(booking.getItemId()).orElseThrow());
        if (itemStorage.getById(booking.getItemId()).getOwner().getId().equals(userId))
            throw new NoSuchElementException("User is the owner");
        if (itemDto.getAvailable().equals(false)) {
            throw new IllegalAccessException("Item is unavailable");
        }
        booking.setBooker(userDto.getId());
        booking.setStatus(BookingStatus.WAITING);
        bookingStorage.save(booking);
        return BookingMapper.toDto(booking, itemDto, userDto);
    }

    public BookingDto book(Long userId, Long bookingId, Boolean approved) throws IllegalAccessException {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow();
        Item item = itemStorage.findById(booking.getItemId()).orElseThrow();
        User booker = userStorage.findById(booking.getBooker()).orElseThrow();
        if (item.getOwner().getId().equals(userId)) {
            if (approved && booking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new IllegalAccessException("Booking is already approved");
            }
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            bookingStorage.save(booking);
            return BookingMapper.toDto(booking, ItemMapper.toDto(item), UserMapper.toDto(booker));
        }
        throw new NoSuchElementException("Booking not found");
    }

    public BookingDto get(Long bookingId, Long userId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow();
        Item item = itemStorage.findById(booking.getItemId()).orElseThrow();
        User user = userStorage.findById(booking.getBooker()).orElseThrow();
        if (booking.getBooker().equals(userId) || item.getOwner().getId().equals(userId)) {
            return BookingMapper.toDto(booking, ItemMapper.toDto(item), UserMapper.toDto(user));
        }
        throw new NoSuchElementException("Booking not found");
    }

    public List<BookingForList> getAllBookingsByOwner(Long userId, String string) {
        try {
            BookingState state = BookingState.valueOf(string);
            User user = userStorage.findById(userId).orElseThrow();
            List<Long> userItemsIds = itemStorage.findAllByOwner(user)
                    .stream()
                    .map(Item::getId)
                    .collect(Collectors.toList());
            List<BookingForList> bookingsByOwner;
            if (!userItemsIds.isEmpty()) {
                switch (state) {
                    case ALL:
                        bookingsByOwner = bookingForListStorage.getAllForOwner(userItemsIds);
                        break;
                    case CURRENT:
                        bookingsByOwner = bookingForListStorage.getCurrentBookingsForOwner(userItemsIds);
                        break;
                    case PAST:
                        bookingsByOwner = bookingForListStorage.getPastBookingsForOwner(userItemsIds);
                        break;
                    case FUTURE:
                        bookingsByOwner = bookingForListStorage.getFutureBookingsForOwner(userItemsIds);
                        break;
                    case WAITING:
                        bookingsByOwner = bookingForListStorage.findBookingByOwnerAndStatusOrderByEndDesc(userItemsIds, BookingStatus.WAITING);
                        break;
                    case REJECTED:
                        bookingsByOwner = bookingForListStorage.findBookingByOwnerAndStatusOrderByEndDesc(userItemsIds, BookingStatus.REJECTED);
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

    public List<BookingForList> getAllBookingsForUserByState(Long userId, String string) {
        try {
            BookingState state = BookingState.valueOf(string);
            Long userIdValue = userStorage.findById(userId).orElseThrow().getId();
            List<BookingForList> bookings = new ArrayList<>();
            switch (state) {
                case ALL:
                    bookings = bookingForListStorage.findAllByBookerOrderByEndDesc(userIdValue);
                    break;
                case CURRENT:
                    bookings = bookingForListStorage.getCurrentBookingsForBooker(userIdValue);
                    break;
                case PAST:
                    bookings = bookingForListStorage.getPastBookingsForBooker(userIdValue);
                    break;
                case FUTURE:
                    bookings = bookingForListStorage.getFutureBookingsForBooker(userIdValue);
                    break;
                case WAITING:
                    bookings = bookingForListStorage.findBookingByBookerAndStatusOrderByEndDesc(userIdValue, BookingStatus.WAITING);
                    break;
                case REJECTED:
                    bookings = bookingForListStorage.findBookingByBookerAndStatusOrderByEndDesc(userIdValue, BookingStatus.REJECTED);
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
