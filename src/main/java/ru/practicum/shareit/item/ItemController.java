package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                       @RequestBody @Valid ItemDto itemDto) throws IllegalAccessException {
        return itemService.createItem(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) throws IllegalAccessException {
        itemService.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDto(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId) throws IllegalAccessException {
        return itemService.getItemDto(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchForItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                         @RequestParam(value = "text") String string) {
        return itemService.searchForItem(userId, string);
    }
}
