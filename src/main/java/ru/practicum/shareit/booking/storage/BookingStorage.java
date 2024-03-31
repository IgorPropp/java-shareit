package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    Booking findFirstByItemIdAndStartBeforeAndStatusIsNotOrderByEndDesc(Long itemId, LocalDateTime end,
                                                                        BookingStatus status);

    Booking findFirstByItemIdAndStartAfterAndStatusIsNotOrderByEndAsc(Long itemId, LocalDateTime start,
                                                                      BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :id AND b.end < :currentTime AND upper(b.status) = UPPER('APPROVED') " +
            "ORDER BY b.start DESC")
    List<Booking> getByBookerIdStatePast(@Param("id") Long id, @Param("currentTime") LocalDateTime currentTime,
                                         Pageable pageRequest);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :id AND b.end < :currentTime AND upper(b.status) = UPPER('APPROVED') " +
            "ORDER BY b.start DESC")
    List<Booking> getByBookerIdStatePast(@Param("id") Long id, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :owner ORDER BY b.start DESC")
    List<Booking> findAllByBookerOrderByEndDesc(@Param("owner") Long owner, Pageable pageRequest);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :booker AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findBookingByBookerAndStatusOrderByEndDesc(@Param("booker") Long booker,
                                                             @Param("status") BookingStatus status,
                                                             Pageable pageRequest);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end > CURRENT_TIMESTAMP " +
            "AND b.start < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> getCurrentBookingsForBooker(@Param("userId") Long userId, Pageable pageRequest);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < CURRENT_TIMESTAMP " +
            "AND b.start < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> getPastBookingsForBooker(@Param("userId") Long userId, Pageable pageRequest);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start >= CURRENT_TIMESTAMP " +
            "AND b.end > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> getFutureBookingsForBooker(@Param("userId") Long userId, Pageable pageRequest);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemsId ORDER BY b.end DESC")
    List<Booking> getAllForOwner(List<Long> itemsId, Pageable pageRequest);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemsId ORDER BY b.end DESC")
    List<Booking> getAllForOwner(List<Long> itemsId);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemsId AND b.end > CURRENT_TIMESTAMP " +
            "AND b.start < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> getCurrentBookingsForOwner(@Param("itemsId") List<Long> itemsId, Pageable pageRequest);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemsId AND b.end < CURRENT_TIMESTAMP " +
            "AND b.start < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> getPastBookingsForOwner(@Param("itemsId") List<Long> itemsId, Pageable pageRequest);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemsId AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> getFutureBookingsForOwner(@Param("itemsId") List<Long> itemsId, Pageable pageRequest);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemsId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findBookingByOwnerAndStatusOrderByEndDesc(@Param("itemsId") List<Long> itemsId,
                                                            @Param("status") BookingStatus status,
                                                            Pageable pageRequest);
}
