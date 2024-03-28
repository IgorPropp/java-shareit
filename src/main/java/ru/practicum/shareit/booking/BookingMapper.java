package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

public class BookingMapper {

    public static Booking fromDto(BookingDto bookingDto) {
        if (bookingDto == null) {
            throw new IllegalStateException();
        }
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());
        booking.setItem(ItemMapper.fromDto(bookingDto.getItem()));
        booking.setBooker(UserMapper.fromDto(bookingDto.getBooker().getId(), bookingDto.getBooker()));
        return booking;
    }

    public static BookingDto toDto(Booking booking, ItemDto itemDto, UserDto userDto) {
        if (booking == null) {
            throw new IllegalStateException();
        }
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setItem(itemDto);
        bookingDto.setBooker(userDto);
        return bookingDto;
    }

    public static BookingDto toDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setItem(ItemMapper.toDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.toDto(booking.getBooker()));
        return bookingDto;
    }

    public static Booking requestToObject(BookingDtoRequest bookingDtoRequest, Item item,
                                          User user, BookingStatus status) {
        if (bookingDtoRequest == null) {
            throw new IllegalStateException();
        }
        Booking booking = new Booking();
        booking.setStart(bookingDtoRequest.getStart());
        booking.setEnd(bookingDtoRequest.getEnd());
        booking.setStatus(status);
        booking.setItem(item);
        booking.setBooker(user);
        return booking;
    }
}
