package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class ItemRequestMapper {
    private final ItemMapper itemMapper;

    public ItemRequestResponseDto toItemRequestOutDto(ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestResponseDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items.stream().map(itemMapper::toDto).collect(Collectors.toList())
        );
    }

    public ItemRequest toItemRequest(ItemRequestDto request, User requester) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(request.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequest;
    }
}