package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.GetAllBookingsDto;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.util.List;

public interface GetAllBookingsStorage extends JpaRepository<GetAllBookingsDto, Long> {
    @Query("SELECT b FROM GetAllBookingsDto b WHERE b.booker.id = :owner ORDER BY b.start DESC")
    List<GetAllBookingsDto> findAllByBookerOrderByEndDesc(@Param("owner") Long owner);

    @Query("SELECT b FROM GetAllBookingsDto b WHERE b.booker.id = :booker AND b.status = :status ORDER BY b.start DESC")
    List<GetAllBookingsDto> findBookingByBookerAndStatusOrderByEndDesc(@Param("booker") Long booker, @Param("status") BookingStatus status);

    @Query("SELECT b FROM GetAllBookingsDto b WHERE b.booker.id = :userId AND b.end > CURRENT_TIMESTAMP " +
            "AND b.start < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<GetAllBookingsDto> getCurrentBookingsForBooker(@Param("userId") Long userId);

    @Query("SELECT b FROM GetAllBookingsDto b WHERE b.booker.id = :userId AND b.end < CURRENT_TIMESTAMP " +
            "AND b.start < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<GetAllBookingsDto> getPastBookingsForBooker(@Param("userId") Long userId);

    @Query("SELECT b FROM GetAllBookingsDto b WHERE b.booker.id = :userId AND b.start >= CURRENT_TIMESTAMP " +
            "AND b.end > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<GetAllBookingsDto> getFutureBookingsForBooker(@Param("userId") Long userId);

    @Query("SELECT b FROM GetAllBookingsDto b WHERE b.item.id IN :itemsId ORDER BY b.end DESC")
    List<GetAllBookingsDto> getAllForOwner(List<Long> itemsId);

    @Query("SELECT b FROM GetAllBookingsDto b WHERE b.item.id IN :itemsId AND b.end > CURRENT_TIMESTAMP " +
            "AND b.start < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<GetAllBookingsDto> getCurrentBookingsForOwner(@Param("itemsId") List<Long> itemsId);

    @Query("SELECT b FROM GetAllBookingsDto b WHERE b.item.id IN :itemsId AND b.end < CURRENT_TIMESTAMP " +
            "AND b.start < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<GetAllBookingsDto> getPastBookingsForOwner(@Param("itemsId") List<Long> itemsId);

    @Query("SELECT b FROM GetAllBookingsDto b WHERE b.item.id IN :itemsId AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<GetAllBookingsDto> getFutureBookingsForOwner(@Param("itemsId") List<Long> itemsId);

    @Query("SELECT b FROM GetAllBookingsDto b WHERE b.item.id IN :itemsId AND b.status = :status ORDER BY b.start DESC")
    List<GetAllBookingsDto> findBookingByOwnerAndStatusOrderByEndDesc(@Param("itemsId") List<Long> itemsId, @Param("status") BookingStatus status);
}
