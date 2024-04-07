package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String SHARERUSERID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(SHARERUSERID) Long userId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemRequestByOwner(@RequestHeader(SHARERUSERID) Long userId) {
        return itemRequestClient.getItemRequestByOwner(userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(SHARERUSERID) Long userId,
                                                     @PathVariable Long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(SHARERUSERID) Long userId,
                                                     @RequestParam(name = "from", defaultValue = "0")
                                                         @PositiveOrZero Integer from,
                                                     @RequestParam(name = "size", defaultValue = "10")
                                                         @Positive Integer size) {
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }
}
