package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static java.time.LocalDateTime.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingStorageTest {

    @Autowired
    private BookingStorage bookingStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;
    private Item savedItem1;
    private Item savedItem2;
    private User savedUser1;
    private User savedUser2;
    private Booking savedBooking1;
    private Booking savedBooking2;
    private Booking savedBooking3;
    private Booking savedBooking4;
    private PageRequest pageRequest;

    @BeforeAll
    void setUp() {
        User user1 = createUser(1L);
        savedUser1 = userStorage.save(user1);
        User user2 = createUser(2L);
        savedUser2 = userStorage.save(user2);

        Item item1 = createItem(1L);
        item1.setOwner(savedUser1);
        savedItem1 = itemStorage.save(item1);
        Item item2 = createItem(2L);
        item2.setOwner(savedUser1);
        savedItem2 = itemStorage.save(item2);

        Booking booking1 = createBooking(1L);
        booking1.setItem(savedItem1);
        booking1.setBooker(savedUser2);
        booking1.setStart(now().minusDays(5));
        booking1.setEnd(now().minusDays(1));
        savedBooking1 = bookingStorage.save(booking1);

        Booking booking2 = createBooking(2L);
        booking2.setItem(savedItem1);
        booking2.setBooker(savedUser2);
        booking2.setStart(now().minusDays(1));
        savedBooking2 = bookingStorage.save(booking2);

        Booking booking3 = createBooking(3L);
        booking3.setItem(savedItem2);
        booking3.setBooker(savedUser1);
        savedBooking3 = bookingStorage.save(booking3);
        pageRequest = PageRequest.of(0, 1);

        Booking booking4 = createBooking(3L);
        booking4.setItem(savedItem2);
        booking4.setBooker(savedUser2);
        booking4.setStart(now().plusDays(1));
        booking4.setEnd(now().plusDays(5));
        savedBooking4 = bookingStorage.save(booking4);

    }

    @AfterAll
    public void clean() {
        bookingStorage.deleteAll();
        itemStorage.deleteAll();
        userStorage.deleteAll();
    }

    @Test
    void testFindAllByBooker() {
        List<Booking> bookings = bookingStorage.findAllByBookerOrderByEndDesc(savedUser2.getId(), pageRequest);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), is(1));
        assertThat(bookings.get(0).getId(), is(is(savedBooking4.getId())));
    }

    @Test
    void testGetPastBookingsForBooker() {
        List<Booking> bookings = bookingStorage.getPastBookingsForBooker(savedUser2.getId(), pageRequest);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), is(1));
        assertThat(bookings.get(0).getId(), is(is(savedBooking1.getId())));
    }

    @Test
    void testGetFutureBookingsForBooker() {
        List<Booking> bookings = bookingStorage.getFutureBookingsForBooker(savedUser2.getId(), pageRequest);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), is(1));
        assertThat(bookings.get(0).getId(), is(is(savedBooking4.getId())));
    }

    @Test
    void testGetCurrentBookingsForBooker() {
        savedBooking2.setEnd(now().minusDays(1));
        List<Booking> bookings = bookingStorage.getCurrentBookingsForBooker(savedUser2.getId(), pageRequest);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), is(1));
        assertThat(bookings.get(0).getId(), is(is(savedBooking2.getId())));
    }

    @Test
    void testGetBookingByBookerAndState() {
        List<Booking> bookings = bookingStorage.findBookingByBookerAndStatusOrderByEndDesc(savedUser1.getId(), BookingStatus.WAITING,
                pageRequest);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), is(1));
        assertThat(bookings.get(0).getId(), is(is(savedBooking3.getId())));
    }

    @Test
    void testGetAllForOwner() {
        List<Booking> bookings = bookingStorage.getAllForOwner(List.of(savedItem2.getId()), pageRequest);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), is(1));
        assertThat(bookings.get(0).getId(), is(is(savedBooking3.getId())));
    }

    @Test
    void testGetPastBookingsForOwner() {
        List<Booking> bookings = bookingStorage.getPastBookingsForOwner(List.of(savedItem1.getId()), pageRequest);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), is(1));
        assertThat(bookings.get(0).getId(), is(is(savedBooking1.getId())));
    }

    @Test
    void testGetFutureBookingsForOwner() {
        List<Booking> bookings = bookingStorage.getFutureBookingsForOwner(List.of(savedItem2.getId()), pageRequest);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), is(1));
        assertThat(bookings.get(0).getId(), is(is(savedBooking3.getId())));
    }

    @Test
    void testGetCurrentBookingsForOwner() {
        savedBooking2.setEnd(now().minusDays(1));
        List<Booking> bookings = bookingStorage.getCurrentBookingsForOwner(List.of(savedItem1.getId()), pageRequest);

        assertThat(bookings, notNullValue());
        assertThat(bookings.size(), is(1));
        assertThat(bookings.get(0).getId(), is(is(savedBooking2.getId())));
    }

    private Item createItem(Long id) {
        return Item.builder()
                .name("name" + id)
                .description("description" + id)
                .available(true)
                .build();
    }

    private User createUser(Long id) {
        return User.builder()
                .name("name" + id)
                .email("email" + id + "@mail.com")
                .build();
    }

    private Booking createBooking(Long id) {
        return Booking.builder()
                .status(BookingStatus.WAITING)
                .start(now().plusDays(id))
                .end(now().plusDays(5 + id))
                .build();
    }
}
