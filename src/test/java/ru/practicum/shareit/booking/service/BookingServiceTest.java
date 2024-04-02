package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private BookingDtoRequest bookingDtoRequest;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private UserDto userDto;
    private ItemDto itemDto;
    private User user;
    private Item item;
    private BookingDto bookingDto;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
        userDto = new UserDto(1L, "userName", "user@email.ru");
        itemDto = new ItemDto("item1", "description1", true, 1L, null);
        user = new User(1L, "user name", "user@email.ru");
        item = new Item(1L, "item1", "description1", true, user, null);
        bookingDto = new BookingDto(1L,
                LocalDateTime.of(2024, 6, 1, 0, 0, 0),
                LocalDateTime.of(2024, 7, 2, 0, 0, 0),
                BookingStatus.WAITING, itemDto, userDto);
        booking = new Booking(1L,
                LocalDateTime.of(2024, 6, 1, 0, 0, 0),
                LocalDateTime.of(2024, 7, 2, 0, 0, 0),
                BookingStatus.WAITING, item, user);
        bookingDtoRequest = new BookingDtoRequest(1L, LocalDateTime.of(2024, 6, 1, 0, 0, 0),
                LocalDateTime.of(2024, 7, 2, 0, 0, 0));
    }

    @Test
    void testCreateBooking() throws IllegalAccessException {
        when(itemStorage.getById(anyLong())).thenReturn(item);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemMapper.toDto(any())).thenReturn(itemDto);
        when(userMapper.toDto(any())).thenReturn(userDto);
        when(bookingMapper.toDto(any(), any(), any())).thenReturn(bookingDto);
        when(bookingStorage.save(any())).thenReturn(booking);

        BookingDto createdBookingDto = bookingService.create(2L, bookingDtoRequest);

        assertEquals(createdBookingDto, bookingDto);
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
        verify(bookingStorage, times(1)).save(any());
    }

    @Test
    void testBook() throws IllegalAccessException {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.save(any())).thenReturn(booking);
        when(itemMapper.toDto(any())).thenReturn(itemDto);
        when(userMapper.toDto(any())).thenReturn(userDto);
        when(bookingMapper.toDto(any(), any(), any())).thenReturn(bookingDto);

        BookingDto saveBooking = bookingService.book(1L, 1L, true);

        assertEquals(saveBooking, bookingDto);
        verify(bookingStorage, times(1)).save(any());
    }

    @Test
    void testGet() throws IllegalAccessException {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemMapper.toDto(any())).thenReturn(itemDto);
        when(userMapper.toDto(any())).thenReturn(userDto);
        when(bookingMapper.toDto(any(), any(), any())).thenReturn(bookingDto);

        BookingDto gotBooking = bookingService.get(1L, 1L);
        assertEquals(gotBooking, bookingDto);

        verify(bookingStorage, times(1)).findById(any());
    }

    @Test
    void testGetAllBookingsForOwner() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findAllByOwner(any())).thenReturn(List.of(item));
        when(bookingStorage.getAllForOwner(any(), any())).thenReturn(List.of(booking));
        when(bookingStorage.getCurrentBookingsForOwner(any(), any())).thenReturn(List.of(booking));
        when(bookingStorage.getPastBookingsForOwner(any(), any())).thenReturn(List.of(booking));
        when(bookingStorage.getFutureBookingsForOwner(any(), any())).thenReturn(List.of(booking));
        when(bookingStorage.findBookingByOwnerAndStatusOrderByEndDesc(any(), any(), any())).thenReturn(List.of(booking));
        when(bookingMapper.toDto(any())).thenReturn(bookingDto);

        List<BookingDto> bookings = bookingService.getAllBookingsByOwner(1L,"ALL", 10, 10);
        assertEquals(bookings, List.of(bookingDto));
        verify(bookingStorage, times(1)).getAllForOwner(any(), any());

        bookings = bookingService.getAllBookingsByOwner(1L,"CURRENT", 10, 10);
        assertEquals(bookings, List.of(bookingDto));
        verify(bookingStorage, times(1)).getCurrentBookingsForOwner(any(), any());

        bookings = bookingService.getAllBookingsByOwner(1L,"PAST", 10, 10);
        assertEquals(bookings, List.of(bookingDto));
        verify(bookingStorage, times(1)).getPastBookingsForOwner(any(), any());

        bookings = bookingService.getAllBookingsByOwner(1L,"FUTURE", 10, 10);
        assertEquals(bookings, List.of(bookingDto));
        verify(bookingStorage, times(1)).getFutureBookingsForOwner(any(), any());

        bookings = bookingService.getAllBookingsByOwner(1L,"WAITING", 10, 10);
        assertEquals(bookings, List.of(bookingDto));
        verify(bookingStorage, times(1)).findBookingByOwnerAndStatusOrderByEndDesc(any(), any(), any());

        bookings = bookingService.getAllBookingsByOwner(1L,"REJECTED", 10, 10);
        assertEquals(bookings, List.of(bookingDto));
        verify(bookingStorage, times(2)).findBookingByOwnerAndStatusOrderByEndDesc(any(), any(), any());
    }

    @Test
    void testAllBookingsForUserByState() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.findAllByBookerOrderByEndDesc(any(), any())).thenReturn(List.of(booking));
        when(bookingStorage.getCurrentBookingsForBooker(any(), any())).thenReturn(List.of(booking));
        when(bookingStorage.getPastBookingsForBooker(any(), any())).thenReturn(List.of(booking));
        when(bookingStorage.getFutureBookingsForBooker(any(), any())).thenReturn(List.of(booking));
        when(bookingStorage.findBookingByBookerAndStatusOrderByEndDesc(any(), any(), any())).thenReturn(List.of(booking));
        when(bookingMapper.toDto(any())).thenReturn(bookingDto);

        List<BookingDto> bookings = bookingService.getAllBookingsForUserByState(1L,"ALL", 10, 10);
        assertEquals(bookings, List.of(bookingDto));
        verify(bookingStorage, times(1)).findAllByBookerOrderByEndDesc(any(), any());

        bookings = bookingService.getAllBookingsForUserByState(1L,"CURRENT", 10, 10);
        assertEquals(bookings, List.of(bookingDto));
        verify(bookingStorage, times(1)).getCurrentBookingsForBooker(any(), any());

        bookings = bookingService.getAllBookingsForUserByState(1L,"PAST", 10, 10);
        assertEquals(bookings, List.of(bookingDto));
        verify(bookingStorage, times(1)).getPastBookingsForBooker(any(), any());

        bookings = bookingService.getAllBookingsForUserByState(1L,"FUTURE", 10, 10);
        assertEquals(bookings, List.of(bookingDto));
        verify(bookingStorage, times(1)).getFutureBookingsForBooker(any(), any());

        bookings = bookingService.getAllBookingsForUserByState(1L,"WAITING", 10, 10);
        assertEquals(bookings, List.of(bookingDto));
        verify(bookingStorage, times(1)).findBookingByBookerAndStatusOrderByEndDesc(any(), any(), any());

        bookings = bookingService.getAllBookingsForUserByState(1L,"REJECTED", 10, 10);
        assertEquals(bookings, List.of(bookingDto));
        verify(bookingStorage, times(2)).findBookingByBookerAndStatusOrderByEndDesc(any(), any(), any());
    }
}