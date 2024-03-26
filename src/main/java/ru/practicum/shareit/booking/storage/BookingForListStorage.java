package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.BookingForList;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.util.List;

public interface BookingForListStorage extends JpaRepository<BookingForList, Long> {
    @Query("SELECT b FROM BookingForList b WHERE b.booker.id = :owner ORDER BY b.start DESC")
    List<BookingForList> findAllByBookerOrderByEndDesc(@Param("owner") Long owner);

    @Query("SELECT b FROM BookingForList b WHERE b.booker.id = :booker AND b.status = :status ORDER BY b.start DESC")
    List<BookingForList> findBookingByBookerAndStatusOrderByEndDesc(@Param("booker") Long booker, @Param("status") BookingStatus status);

    @Query("SELECT b FROM BookingForList b WHERE b.booker.id = :userId AND b.end > CURRENT_TIMESTAMP " +
            "AND b.start < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<BookingForList> getCurrentBookingsForBooker(@Param("userId") Long userId);

    @Query("SELECT b FROM BookingForList b WHERE b.booker.id = :userId AND b.end < CURRENT_TIMESTAMP " +
            "AND b.start < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<BookingForList> getPastBookingsForBooker(@Param("userId") Long userId);

    @Query("SELECT b FROM BookingForList b WHERE b.booker.id = :userId AND b.start >= CURRENT_TIMESTAMP " +
            "AND b.end > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<BookingForList> getFutureBookingsForBooker(@Param("userId") Long userId);

    @Query("SELECT b FROM BookingForList b WHERE b.item.id IN :itemsId ORDER BY b.end DESC")
    List<BookingForList> getAllForOwner(List<Long> itemsId);

    @Query("SELECT b FROM BookingForList b WHERE b.item.id IN :itemsId AND b.end > CURRENT_TIMESTAMP " +
            "AND b.start < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<BookingForList> getCurrentBookingsForOwner(@Param("itemsId") List<Long> itemsId);

    @Query("SELECT b FROM BookingForList b WHERE b.item.id IN :itemsId AND b.end < CURRENT_TIMESTAMP " +
            "AND b.start < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<BookingForList> getPastBookingsForOwner(@Param("itemsId") List<Long> itemsId);

    @Query("SELECT b FROM BookingForList b WHERE b.item.id IN :itemsId AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<BookingForList> getFutureBookingsForOwner(@Param("itemsId") List<Long> itemsId);

    @Query("SELECT b FROM BookingForList b WHERE b.item.id IN :itemsId AND b.status = :status ORDER BY b.start DESC")
    List<BookingForList> findBookingByOwnerAndStatusOrderByEndDesc(@Param("itemsId") List<Long> itemsId, @Param("status") BookingStatus status);
}
