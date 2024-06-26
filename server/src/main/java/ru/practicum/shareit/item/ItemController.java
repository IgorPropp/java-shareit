package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String SHARERUSERID = "X-Sharer-User-Id";

    @GetMapping
    public List<BookingItemDto> getItems(@RequestHeader(name = SHARERUSERID) Long userId) {
        return itemService.getItems(userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(name = SHARERUSERID) Long userId,
                       @RequestBody @Valid ItemDto itemDto) throws IllegalAccessException {
        return itemService.createItem(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(name = SHARERUSERID) Long userId,
                           @PathVariable Long itemId) throws IllegalAccessException {
        itemService.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(name = SHARERUSERID) Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public BookingItemDto getItemDto(@RequestHeader(name = SHARERUSERID) Long userId,
                              @PathVariable Long itemId) throws IllegalAccessException {
        return itemService.getItemDto(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchForItem(@RequestHeader(name = SHARERUSERID) Long userId,
                                       @RequestParam(value = "text") String string) {
        return itemService.searchForItem(userId, string);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId, @RequestBody CommentDto commentDto) throws IllegalAccessException {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
