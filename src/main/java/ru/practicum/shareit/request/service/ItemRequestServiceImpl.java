package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage itemRequestStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemRequestResponseDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requester = userStorage.findById(userId).orElseThrow();
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requester);
        itemRequestStorage.save(itemRequest);
        return ItemRequestMapper.toItemRequestOutDto(itemRequest, List.of());
    }

    public List<ItemRequestResponseDto> getItemRequestByOwner(Long userId) {
        userStorage.findById(userId).orElseThrow();
        List<ItemRequest> requests = itemRequestStorage.findAllByRequesterIdOrderByCreatedDesc(userId);
        return toListResponseDto(requests);
    }

    public List<ItemRequestResponseDto> getAllItemRequests(Long userId, Pageable pageRequest) {
        userStorage.findById(userId).orElseThrow();
        List<ItemRequest> requests = itemRequestStorage.findAllByOtherUsers(userId, pageRequest);
        return toListResponseDto(requests);
    }

    public ItemRequestResponseDto getItemRequestById(Long userId, Long requestId) {
        userStorage.findById(userId).orElseThrow();
        ItemRequest itemRequest = itemRequestStorage.findById(requestId).orElseThrow(() ->
                new NoSuchElementException(String.format("Запрос с id = %s не найден!", requestId)));
        return ItemRequestMapper.toItemRequestOutDto(itemRequest,
                itemStorage.findAllByRequestId(itemRequest.getId()));
    }

    private List<ItemRequestResponseDto> toListResponseDto(List<ItemRequest> requests) {
        List<ItemRequestResponseDto> requestsOut;
        requestsOut = requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestOutDto(request,
                        itemStorage.findAllByRequestId(request.getId())))
                .collect(Collectors.toList());
        return requestsOut;
    }
}