package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

@AllArgsConstructor
@Component
public class BookingMapper {
    @Autowired
    private final ItemMapper itemMapper;
    @Autowired
    private final UserMapper userMapper;

    public Booking fromDto(BookingDto bookingDto) {
        if (bookingDto == null) {
            throw new IllegalStateException();
        }
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());
        booking.setItem(itemMapper.fromDto(bookingDto.getItem()));
        booking.setBooker(userMapper.fromDto(bookingDto.getBooker().getId(), bookingDto.getBooker()));
        return booking;
    }

    public BookingDto toDto(Booking booking, ItemDto itemDto, UserDto userDto) {
        if (booking == null) {
            return null;
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

    public BookingDto toDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setItem(itemMapper.toDto(booking.getItem()));
        bookingDto.setBooker(userMapper.toDto(booking.getBooker()));
        return bookingDto;
    }

    public Booking requestToObject(BookingDtoRequest bookingDtoRequest, Item item,
                                          User user, BookingStatus status) {
        if (bookingDtoRequest == null) {
            return null;
        }
        Booking booking = new Booking();
        booking.setStart(bookingDtoRequest.getStart());
        booking.setEnd(bookingDtoRequest.getEnd());
        booking.setStatus(status);
        booking.setItem(item);
        booking.setBooker(user);
        return booking;
    }

    public BookingDtoRequest objectToRequest(Booking booking) {
        BookingDtoRequest bdr = new BookingDtoRequest();
        bdr.setItemId(booking.getId());
        bdr.setStart(booking.getStart());
        bdr.setEnd(booking.getEnd());
        return bdr;
    }
}
