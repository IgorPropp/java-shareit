package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {

    ItemDto createItem(Long userId, ItemDto itemDto);

    List<ItemDto> getItems(Long userId);

    void deleteItem(Long userId, Long itemId) throws IllegalAccessException;

    ItemDto updateItem(Long userId, ItemDto itemDto);

    ItemDto getItemDto(Long userId, Long itemId) throws IllegalAccessException;

    List<ItemDto> searchForItem(Long userId, String string);

}
