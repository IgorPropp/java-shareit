package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final CommentStorage commentStorage;
    private final BookingStorage bookingStorage;
    private final ItemMapper itemMapper;

    public List<BookingItemDto> getItems(Long userId) {
        User user = userStorage.getById(userId);
        List<Item> items = itemStorage.findAllByOwner(user);
        List<BookingItemDto> bookingItems = new ArrayList<>();
        for (Item item : items) {
            List<CommentDto> comments = commentStorage.getByItem_IdOrderByCreatedDesc(item.getId())
                    .stream()
                    .map(CommentMapper::toDto).collect(Collectors.toList());
            Booking lastBooking = bookingStorage.findFirstByItemIdAndStartBeforeAndStatusIsNotOrderByEndDesc(
                    item.getId(), LocalDateTime.now(), BookingStatus.REJECTED);
            Booking nextBooking = bookingStorage.findFirstByItemIdAndStartAfterAndStatusIsNotOrderByEndAsc(
                    item.getId(), LocalDateTime.now(), BookingStatus.REJECTED);
            bookingItems.add(new BookingItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                    ((lastBooking == null) ? null : new BookingItemDto.Booking(lastBooking.getId(),
                            lastBooking.getBooker())),
                    ((nextBooking == null) ? null : new BookingItemDto.Booking(nextBooking.getId(),
                            nextBooking.getBooker())), comments));
        }
        bookingItems.sort(Comparator.comparing(BookingItemDto::getId));
        return bookingItems;
    }

    public ItemDto createItem(Long userId, ItemDto itemDto) {
        userStorage.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Item item = itemMapper.fromDto(itemDto);
        item.setOwner(userStorage.getById(userId));
        itemStorage.save(item);
        itemDto.setId(item.getId());
        return itemDto;
    }

    public void deleteItem(Long userId, Long itemId) {
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (item.getOwner().getId().equals(userId)) {
            itemStorage.deleteById(itemId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Incorrect user ID or item is not exist");
        }
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
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
            return itemMapper.toDto(itemStorage.save(item));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user doesn't own this item");
        }
    }

    public BookingItemDto getItemDto(Long userId, Long itemId) {
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
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
                            lastBooking.getBooker())),
                    ((nextBooking == null) ? null : new BookingItemDto.Booking(nextBooking.getId(),
                            nextBooking.getBooker())), comments);
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
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new IllegalArgumentException("This comment is empty or blank");
        }
        User user = userStorage.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        Comment comment = CommentMapper.fromDto(commentDto, item, user);
        List<Booking> booking = bookingStorage.getByBookerIdStatePast(comment.getUser().getId(), LocalDateTime.now());
        if (booking.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user has not booked anything");
        }
        comment.setCreated(LocalDateTime.now());
        commentStorage.save(comment);
        return CommentMapper.toDto(comment);
    }
}
