package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ItemServiceTest {
    @Mock
    ItemStorage itemStorage;
    @Mock
    UserStorage userStorage;
    @Mock
    BookingStorage bookingStorage;
    @Mock
    CommentStorage commentStorage;
    @Mock
    ItemRequestStorage itemRequestStorage;
    @InjectMocks
    ItemServiceImpl itemService;
    User user;
    Item item;
    ItemDto itemDto;
    Item updatedItem;
    ItemDto updatedItemDto;
    ItemRequest itemRequest;
    @Mock
    ItemMapper itemMapper;
    @Mock
    CommentMapper commentMapper;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "User", "user@email.ru");
        itemRequest = new ItemRequest(1L, "request description", user,
                LocalDateTime.now().plusMonths(3));
        item = new Item(1L, "Item", "item description", true, user, null);
        itemDto = new ItemDto("Item", "item description", true, 1L, null);
        updatedItem = new Item(item.getId(), "updateItemName", "updateItemDescription",
                item.getAvailable(), item.getOwner(), item.getRequest());
        updatedItemDto = new ItemDto("updateItemName", "updateItemDescription", item.getAvailable(), 1L, null);
    }

    @Test
    void testCreateItem() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestStorage.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemStorage.save(item)).thenReturn(item);
        when(itemMapper.fromDto(any())).thenReturn(item);

        ItemDto itemDtoCreated = itemService.createItem(1L, itemDto);
        assertNotNull(itemDtoCreated);
        assertEquals(ItemDto.class, itemDtoCreated.getClass());
        assertEquals(item.getId(), itemDtoCreated.getId());
        assertEquals(item.getName(), itemDtoCreated.getName());
        assertEquals(item.getDescription(), itemDtoCreated.getDescription());
        assertEquals(item.getAvailable(), itemDtoCreated.getAvailable());
    }

    @Test
    void testUpdateItem() {
        Item updatedItem = new Item(item.getId(), "updateItemName", "updateItemDescription",
                item.getAvailable(), item.getOwner(), item.getRequest());
        when(itemStorage.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(itemStorage.save(updatedItem)).thenReturn(updatedItem);
        when(itemMapper.toDto(any())).thenReturn(updatedItemDto);

        ItemDto itemDto = itemMapper.toDto(updatedItem);
        ItemDto updateItem = itemService.updateItem(1L, itemDto.getId(), itemDto);

        assertNotNull(updateItem);
        assertEquals(ItemDto.class, updateItem.getClass());
        assertEquals(updatedItem.getId(), updateItem.getId());
        assertEquals(updatedItem.getName(), updateItem.getName());
        assertEquals(updatedItem.getDescription(), updateItem.getDescription());
        assertEquals(updatedItem.getAvailable(), updateItem.getAvailable());

    }

    @Test
    void testFindById() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentStorage.getByItem_IdOrderByCreatedDesc(anyLong())).thenReturn(List.of());
        when(commentMapper.toDto(any())).thenReturn(null);

        BookingItemDto itemDto = new BookingItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(),
                null, null, Collections.emptyList());

        BookingItemDto itemDto1 = itemService.getItemDto(1L, 1L);
        assertNotNull(itemDto1);
        assertEquals(BookingItemDto.class, itemDto1.getClass());
        assertEquals(itemDto.getId(), itemDto1.getId());
        assertEquals(itemDto.getName(), itemDto1.getName());
        assertEquals(itemDto.getDescription(), itemDto1.getDescription());
        assertEquals(itemDto.getAvailable(), itemDto1.getAvailable());
    }

    @Test
    void testDeleteItem() {
        long userId = 1L;
        long itemId = 1L;
        User user = new User(userId, "User", "user@email.ru");
        Item item = new Item(itemId, "Item", "item description", true, user, null);
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        itemService.deleteItem(userId, itemId);
        verify(itemStorage, times(1)).deleteById(itemId);
    }

    @Test
    void testGetItems() {
        List<Item> items = new ArrayList<>();
        items.add(new Item(1L, "Item1", "Description1", true, user, null));
        items.add(new Item(2L, "Item2", "Description2", true, user, null));

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findAllByOwner(user)).thenReturn(items);
        when(commentStorage.getByItem_IdIn(anyList())).thenReturn(new ArrayList<>());
        when(bookingStorage.getAllForOwner(anyList())).thenReturn(new ArrayList<>());
        when(commentMapper.toDto(any())).thenReturn(null);

        List<BookingItemDto> result = itemService.getItems(user.getId());

        assertEquals(2, result.size());
    }

    @Test
    void testAddComment() throws IllegalAccessException {
        CommentDto commentDto = new CommentDto(1L, "Test comment", "Test author", LocalDateTime.now());
        Comment comment = new Comment(1L, "Test comment", item, user, LocalDateTime.now());
        Booking booking = new Booking(1L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED, item, user);

        when(userStorage.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemStorage.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingStorage.getByBookerIdStatePast(any(), any())).thenReturn(Collections.singletonList(booking));
        when(commentMapper.fromDto(any(), any(), any())).thenReturn(comment);
        when(commentMapper.toDto(any())).thenReturn(commentDto);
        when(commentStorage.save(any())).thenReturn(comment);

        CommentDto result = itemService.addComment(user.getId(), item.getId(), commentDto);

        assertNotNull(result);
        assertEquals(commentDto.getText(), result.getText());
        assertNotNull(result.getCreated());
        verify(commentStorage, times(1)).save(any());
    }
}
