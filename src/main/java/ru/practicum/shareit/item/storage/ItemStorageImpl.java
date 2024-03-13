package ru.practicum.shareit.item.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private Long id = 1L;
    private final List<Item> items = new ArrayList<>();
    private final UserStorage userStorage;

    public ItemStorageImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<ItemDto> getItems(Long userId) {
        List<ItemDto> itemsByUserId = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner().equals(userId)) {
                itemsByUserId.add(ItemMapper.toDto(item));
            }
        }
        return itemsByUserId;
    }

    public ItemDto createItem(Long userId, ItemDto itemDto) {
        for (UserDto user : userStorage.getAllUsers()) {
            if (user.getId().equals(userId)) {
                itemDto.setId(id);
                Item item = ItemMapper.fromDto(userId, itemDto);
                item.setOwner(userId);
                items.add(item);
                id++;
                return itemDto;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    public void deleteItem(Long userId, Long itemId) throws IllegalAccessException {
        Iterator<Item> itemIterator = items.listIterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            if (item.getId().equals(userId) && item.getId().equals(itemId)) {
                itemIterator.remove();
                return;
            }
        }
        throw new IllegalAccessException("Incorrect id");
    }

    public ItemDto updateItem(Long userId, ItemDto itemDto) {
        for (Item item : items) {
            if (item.getOwner().equals(userId)) {
                if (itemDto.getName() != null) {
                    item.setName(itemDto.getName());
                }
                if (itemDto.getDescription() != null) {
                    item.setDescription(itemDto.getDescription());
                }
                if (itemDto.getAvailable() != null) {
                    item.setAvailable(itemDto.getAvailable());
                }
                return ItemMapper.toDto(item);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Incorrect user ID or item is not exist");
    }

    public ItemDto getItemDto(Long userId, Long itemId) throws IllegalAccessException {
        for (Item item : items) {
            if (item.getId().equals(itemId)) {
                return ItemMapper.toDto(item);
            }
        }
        throw new IllegalAccessException("Incorrect id");
    }

    public List<ItemDto> searchForItem(Long userId, String string) {
        List<ItemDto> itemsAfterSearch = new ArrayList<>();
        if (string.isEmpty()) return itemsAfterSearch;

        for (Item item : items) {
            if (item.getName().toLowerCase().contains(string) &&
                    item.getAvailable()) {
                itemsAfterSearch.add(ItemMapper.toDto(item));
            } else if (item.getDescription().toLowerCase().contains(string) &&
                    item.getAvailable()) {
                itemsAfterSearch.add(ItemMapper.toDto(item));
            }
        }
        return itemsAfterSearch;
    }

}
