package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentStorage;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final CommentStorage commentStorage;
    private final BookingStorage bookingStorage;

    public List<BookingItemDto> getItems(Long userId) {
        User user = userStorage.findById(userId).orElseThrow();
        List<Item> items = itemStorage.findAllByOwner(user);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingStorage.getAllForOwner(itemIds);
        List<CommentDto> comments = commentStorage.getByItem_IdIn(itemIds).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        List<BookingItemDto> bookingItems = new ArrayList<>();
        for (Item item : items) {
            Booking lastBooking = bookings.stream()
                    .filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED))
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                    .min((o1, o2) -> o2.getStart().compareTo(o1.getStart()))
                    .orElse(null);
            Booking nextBooking = bookings.stream()
                    .filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED))
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            bookingItems.add(new BookingItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                    ((lastBooking == null) ? null : new BookingItemDto.Booking(lastBooking.getId(),
                            lastBooking.getBooker().getId())),
                    ((nextBooking == null) ? null : new BookingItemDto.Booking(nextBooking.getId(),
                            nextBooking.getBooker().getId())), comments));
        }
        bookingItems.sort(Comparator.comparing(BookingItemDto::getId));
        return bookingItems;
    }

    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userStorage.findById(userId).orElseThrow();
        Item item = ItemMapper.fromDto(itemDto);
        item.setOwner(user);
        itemStorage.save(item);
        itemDto.setId(item.getId());
        return itemDto;
    }

    public void deleteItem(Long userId, Long itemId) {
        Item item = itemStorage.findById(itemId).orElseThrow();
        if (item.getOwner().getId().equals(userId)) {
            itemStorage.deleteById(itemId);
        } else {
            throw new NoSuchElementException("Incorrect user ID or item is not exist");
        }
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemStorage.findById(itemId).orElseThrow();
        if (item.getOwner().getId().equals(userId)) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            return ItemMapper.toDto(itemStorage.save(item));
        } else {
            throw new NoSuchElementException("This user doesn't own this item");
        }
    }

    public BookingItemDto getItemDto(Long userId, Long itemId) {
        Item item = itemStorage.findById(itemId).orElseThrow();
        List<CommentDto> comments = commentStorage.getByItem_IdOrderByCreatedDesc(item.getId())
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingStorage.findFirstByItemIdAndStartBeforeAndStatusIsNotOrderByEndDesc(
                    item.getId(), LocalDateTime.now(), BookingStatus.REJECTED);
            Booking nextBooking = bookingStorage.findFirstByItemIdAndStartAfterAndStatusIsNotOrderByEndAsc(
                    item.getId(), LocalDateTime.now(), BookingStatus.REJECTED);
            return new BookingItemDto(
                    itemId, item.getName(), item.getDescription(), item.getAvailable(),
                    ((lastBooking == null) ? null : new BookingItemDto.Booking(lastBooking.getId(),
                            lastBooking.getBooker().getId())),
                    ((nextBooking == null) ? null : new BookingItemDto.Booking(nextBooking.getId(),
                            nextBooking.getBooker().getId())), comments);
            } else {
            return new BookingItemDto(itemId, item.getName(), item.getDescription(), item.getAvailable(),
                    null, null, comments);
            }
    }

    public List<ItemDto> searchForItem(Long userId, String string) {
        if (string.isEmpty()) {
            return new ArrayList<>();
        }
        return itemStorage.findByNameOrDescriptionContainingIgnoreCase(string.toLowerCase()).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) throws IllegalAccessException {
        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new IllegalArgumentException("This comment is empty or blank");
        }
        User user = userStorage.findById(userId).orElseThrow();
        Item item = itemStorage.findById(itemId).orElseThrow();
        Comment comment = CommentMapper.fromDto(commentDto, item, user);
        List<Booking> booking = bookingStorage.getByBookerIdStatePast(userId, LocalDateTime.now());
        if (booking.isEmpty()) {
            throw new IllegalAccessException("The user has not booked anything");
        }
        comment.setCreated(LocalDateTime.now());
        commentStorage.save(comment);
        return CommentMapper.toDto(comment);
    }
}
