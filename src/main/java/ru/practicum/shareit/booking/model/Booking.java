package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    @Column(name = "end_time")
    private LocalDateTime end;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @OneToOne
    @JoinColumn(name = "booker")
    private User booker;
}
