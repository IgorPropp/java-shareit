package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    public List<ItemDto> getItems(Long userId) {
        return itemStorage.findAllByOwner(userStorage.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                .stream()
                .map(itemMapper::toDto).collect(Collectors.toList());
    }

    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userStorage.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Item item = itemMapper.fromDto(itemDto);
        item.setOwner(userStorage.getById(userId));
        itemStorage.save(item);
        itemDto.setId(item.getId());
        return itemDto;
    }

    public void deleteItem(Long userId, Long itemId) throws IllegalAccessException {
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

    public ItemDto getItemDto(Long userId, Long itemId) throws IllegalAccessException {
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (item.getOwner().getId().equals(userId)) {
            return itemMapper.toDto(item);
        } else {
            throw new IllegalAccessException("This user doesn't own this item");
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
}
