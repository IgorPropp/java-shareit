package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto);

    void deleteItem(Long userId, Long itemId) throws IllegalAccessException;

    List<ItemDto> getItems(Long userId);

    ItemDto getItemDto(Long userId, Long itemId) throws IllegalAccessException;

    List<ItemDto> searchForItem(Long userId, String string);
}
