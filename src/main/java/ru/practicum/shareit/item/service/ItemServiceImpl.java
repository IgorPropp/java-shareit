package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public List<ItemDto> getItems(Long userId) {
        return itemStorage.getItems(userId);
    }

    public ItemDto createItem(Long userId, ItemDto itemDto) {
        if (userStorage.getUser(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
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
