package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final ItemService itemService;
    private final UserService userService;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final ItemMapper itemMapper;

    public BookingDto create(Long userId, Booking booking) throws IllegalAccessException {
        if (bookingDatesAreValid(booking)) {
            throw new IllegalStateException();
        }
        UserDto userDto = userService.getUser(userId);
        ItemDto itemDto = itemService.getItemDto(userId, booking.getItemId());
        if (itemDto.getAvailable().equals(false)) {
            throw new IllegalStateException();
        }
        booking.setBookerId(userDto.getId());
        booking.setStatus(BookingStatus.WAITING);
        bookingStorage.save(booking);
        return BookingMapper.toDto(booking, itemDto, userDto);
    }

    public BookingDto book(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        Item item = itemStorage.findById(booking.getItemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        User booker = userStorage.findById(booking.getBookerId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booker not found"));
        if (item.getOwner().getId().equals(userId)) {
            if (approved && booking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new IllegalStateException("Booking already approved");
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
        User user = userStorage.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        if (booking.getBookerId().equals(userId) || item.getOwner().getId().equals(userId)) {
            return BookingMapper.toDto(booking, itemMapper.toDto(item), UserMapper.toDto(user));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found");
    }

    private boolean bookingDatesAreValid(Booking booking) {
        return booking.getStart() != null && booking.getEnd() != null && !booking.getStart().isAfter(booking.getEnd()) &&
                !booking.getEnd().isBefore(booking.getStart()) && booking.getStart() != booking.getEnd() &&
                !booking.getStart().equals(booking.getEnd()) && !booking.getStart().isBefore(LocalDateTime.now());
    }

}
