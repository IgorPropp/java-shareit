package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto createItemRequest(@RequestHeader(HEADER_USER_ID) Long userId,
                                                    @Valid @RequestBody ItemRequestDto requestInDto) {
        return itemRequestService.createItemRequest(userId, requestInDto);
    }

    @GetMapping()
    public List<ItemRequestResponseDto> getItemRequestByOwner(@RequestHeader(HEADER_USER_ID) Long userId) {
        return itemRequestService.getItemRequestByOwner(userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestResponseDto getItemRequestById(@RequestHeader(HEADER_USER_ID) Long userId,
                                                     @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllItemRequests(@RequestHeader(HEADER_USER_ID) Long userId,
                                                           @RequestParam(name = "from", defaultValue = "0")
                                                           @PositiveOrZero Integer from,
                                                           @RequestParam(name = "size", defaultValue = "10")
                                                               @Positive Integer size) {
        return itemRequestService.getAllItemRequests(userId, PageRequest.of(from / size, size));
    }
}