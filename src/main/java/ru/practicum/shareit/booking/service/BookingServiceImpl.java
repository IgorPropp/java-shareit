package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
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

    public BookingDto create(Long userId, BookingDtoRequest bookingDtoRequest) throws IllegalAccessException {
        if (!bookingDatesAreValid(bookingDtoRequest)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking time is incorrect");
        }
        UserDto userDto = UserMapper.toDto(userStorage.findById(userId).orElseThrow());
        ItemDto itemDto = ItemMapper.toDto(itemStorage.findById(bookingDtoRequest.getItemId()).orElseThrow());
        if (itemStorage.getById(bookingDtoRequest.getItemId()).getOwner().getId().equals(userId))
            throw new NoSuchElementException("Booker is the owner");
        if (itemDto.getAvailable().equals(false)) {
            throw new IllegalAccessException("Item is unavailable");
        }
        Booking booking = BookingMapper.requestToObject(bookingDtoRequest, ItemMapper.fromDto(itemDto),
                UserMapper.fromDto(userDto.getId(), userDto), BookingStatus.WAITING);
        bookingStorage.save(booking);
        return BookingMapper.toDto(booking, itemDto, userDto);
    }

    public BookingDto book(Long userId, Long bookingId, Boolean approved) throws IllegalAccessException {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow();
        Item item = itemStorage.findById(booking.getItem().getId()).orElseThrow();
        User booker = userStorage.findById(booking.getBooker().getId()).orElseThrow();
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
        Item item = itemStorage.findById(booking.getItem().getId()).orElseThrow();
        User user = userStorage.findById(booking.getBooker().getId()).orElseThrow();
        if (booking.getBooker().getId().equals(userId) || item.getOwner().getId().equals(userId)) {
            return BookingMapper.toDto(booking, ItemMapper.toDto(item), UserMapper.toDto(user));
        }
        throw new NoSuchElementException("Booking not found");
    }

    public List<BookingDto> getAllBookingsByOwner(Long userId, String string, int page, int size) {
        try {
            BookingState state = BookingState.valueOf(string);
            User user = userStorage.findById(userId).orElseThrow();
            Sort sort = Sort.by(Sort.Direction.DESC, "start");
            Pageable pageRequest = PageRequest.of(page, size, sort);
            List<Long> userItemsIds = itemStorage.findAllByOwner(user)
                    .stream()
                    .map(Item::getId)
                    .collect(Collectors.toList());
            List<Booking> bookingsByOwner;
            if (!userItemsIds.isEmpty()) {
                switch (state) {
                    case ALL:
                        bookingsByOwner = bookingStorage.getAllForOwner(userItemsIds, pageRequest);
                        break;
                    case CURRENT:
                        bookingsByOwner = bookingStorage.getCurrentBookingsForOwner(userItemsIds, pageRequest);
                        break;
                    case PAST:
                        bookingsByOwner = bookingStorage.getPastBookingsForOwner(userItemsIds, pageRequest);
                        break;
                    case FUTURE:
                        bookingsByOwner = bookingStorage.getFutureBookingsForOwner(userItemsIds, pageRequest);
                        break;
                    case WAITING:
                        bookingsByOwner = bookingStorage.findBookingByOwnerAndStatusOrderByEndDesc(userItemsIds,
                                BookingStatus.WAITING, pageRequest);
                        break;
                    case REJECTED:
                        bookingsByOwner = bookingStorage.findBookingByOwnerAndStatusOrderByEndDesc(userItemsIds,
                                BookingStatus.REJECTED, pageRequest);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            } else {
                throw new IllegalStateException("User has no items");
            }
            return bookingsByOwner.stream()
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    public List<BookingDto> getAllBookingsForUserByState(Long userId, String string, int page, int size) {
        try {
            BookingState state = BookingState.valueOf(string);
            Long userIdValue = userStorage.findById(userId).orElseThrow().getId();
            Sort sort = Sort.by(Sort.Direction.DESC, "start");
            Pageable pageRequest = PageRequest.of(page, size, sort);
            List<Booking> bookings = new ArrayList<>();
            switch (state) {
                case ALL:
                    bookings = bookingStorage.findAllByBookerOrderByEndDesc(userIdValue, pageRequest);
                    break;
                case CURRENT:
                    bookings = bookingStorage.getCurrentBookingsForBooker(userIdValue, pageRequest);
                    break;
                case PAST:
                    bookings = bookingStorage.getPastBookingsForBooker(userIdValue, pageRequest);
                    break;
                case FUTURE:
                    bookings = bookingStorage.getFutureBookingsForBooker(userIdValue, pageRequest);
                    break;
                case WAITING:
                    bookings = bookingStorage.findBookingByBookerAndStatusOrderByEndDesc(userIdValue,
                            BookingStatus.WAITING, pageRequest);
                    break;
                case REJECTED:
                    bookings = bookingStorage.findBookingByBookerAndStatusOrderByEndDesc(userIdValue,
                            BookingStatus.REJECTED, pageRequest);
                    break;
            }
            return bookings.stream()
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private boolean bookingDatesAreValid(BookingDtoRequest booking) {
        return booking.getStart() != null && booking.getEnd() != null && !booking.getStart().isAfter(booking.getEnd()) &&
                !booking.getEnd().isBefore(booking.getStart()) && booking.getStart() != booking.getEnd() &&
                !booking.getStart().equals(booking.getEnd()) && !booking.getStart().isBefore(LocalDateTime.now());
    }

}
