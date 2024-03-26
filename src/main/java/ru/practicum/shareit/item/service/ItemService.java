package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto) throws IllegalAccessException;

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    void deleteItem(Long userId, Long itemId) throws IllegalAccessException;

    List<BookingItemDto> getItems(Long userId);

    BookingItemDto getItemDto(Long userId, Long itemId) throws IllegalAccessException;

    List<ItemDto> searchForItem(Long userId, String string);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) throws IllegalAccessException;
}
