package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;
    private static final String SHARERUSERID = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(SHARERUSERID) Long userId) {
        return itemClient.getItems(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(SHARERUSERID) Long userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(SHARERUSERID) Long userId, @PathVariable Long itemId) {
        return itemClient.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(SHARERUSERID) Long userId, @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemDto(@RequestHeader(SHARERUSERID) Long userId, @PathVariable Long itemId) {
        return itemClient.getItemDto(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchForItem(@RequestHeader(SHARERUSERID) Long userId,
                                                @RequestParam("text") String string) {
        return itemClient.searchForItem(userId, string);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(SHARERUSERID) Long userId, @PathVariable Long itemId,
                                             @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}