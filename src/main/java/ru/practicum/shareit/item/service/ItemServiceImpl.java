package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    public List<ItemDto> getItems(Long userId) {
        return itemStorage.getItems(userId);
    }

    public ItemDto createItem(Long userId, ItemDto itemDto) {
        return itemStorage.createItem(userId, itemDto);
    }

    public void deleteItem(Long userId, Long itemId) throws IllegalAccessException {
        itemStorage.deleteItem(userId, itemId);
    }

    public ItemDto updateItem(Long userId, ItemDto itemDto) {
        return itemStorage.updateItem(userId, itemDto);
    }

    public ItemDto getItemDto(Long userId, Long itemId) throws IllegalAccessException {
        return itemStorage.getItemDto(userId, itemId);
    }

    public List<ItemDto> searchForItem(Long userId, String string) {
        return itemStorage.searchForItem(userId, string.toLowerCase());
    }
}
