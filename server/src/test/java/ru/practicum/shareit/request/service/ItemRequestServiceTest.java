package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ItemRequestServiceTest {

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;
    @Mock
    ItemRequestStorage itemRequestStorage;
    @Mock
    UserStorage userStorage;
    @Mock
    ItemStorage itemStorage;
    @Mock
    ItemRequestMapper itemRequestMapper;
    User user;
    Item item;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;
    ItemRequestResponseDto itemRequestResponseDto;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "User", "user@email.ru");
        itemRequest = new ItemRequest(1L, "request description", user,
                LocalDateTime.of(2022, 10, 10, 10, 10, 10));
        item = new Item(1L, "Item", "item description", true, user, null);
        itemRequestDto = new ItemRequestDto(1L, "request description", user,
                LocalDateTime.of(2022, 10, 10, 10, 10, 10));
        itemRequestResponseDto = new ItemRequestResponseDto(1L, "request description",
                LocalDateTime.of(2022, 10, 10, 10, 10, 10), null);
    }

    @Test
    void testCreateItemRequest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestMapper.toItemRequest(any(), any())).thenReturn(itemRequest);
        when(itemRequestStorage.save(any())).thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestOutDto(any(), any())).thenReturn(itemRequestResponseDto);
        when(itemStorage.findAllByRequestId(anyLong())).thenReturn(null);

        ItemRequestResponseDto response = itemRequestService.createItemRequest(user.getId(), itemRequestDto);
        assertNotNull(response);
        assertEquals(ItemRequestResponseDto.class, response.getClass());
        assertEquals(itemRequest.getId(), response.getId());
        assertEquals(itemRequest.getDescription(), response.getDescription());
        assertEquals(itemRequest.getCreated(), response.getCreated());

        verify(itemRequestStorage, times(1)).save(any());
    }

    @Test
    void testGetItemRequestByOwner() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestStorage.findAllByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));
        when(itemRequestMapper.toItemRequestOutDto(any(), any())).thenReturn(itemRequestResponseDto);
        when(itemStorage.findAllByRequestId(anyLong())).thenReturn(null);

        List<ItemRequestResponseDto> response = itemRequestService.getItemRequestByOwner(user.getId());
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(ItemRequestResponseDto.class, response.get(0).getClass());
        assertEquals(itemRequest.getId(), response.get(0).getId());
        assertEquals(itemRequest.getDescription(), response.get(0).getDescription());
        assertEquals(itemRequest.getCreated(), response.get(0).getCreated());

        verify(itemRequestStorage, times(1)).findAllByRequesterIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void testGetAllItemRequests() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestStorage.findAllByOtherUsers(anyLong(), any())).thenReturn(List.of(itemRequest));
        when(itemRequestMapper.toItemRequestOutDto(any(), any())).thenReturn(itemRequestResponseDto);
        when(itemStorage.findAllByRequestId(anyLong())).thenReturn(null);

        List<ItemRequestResponseDto> response = itemRequestService.getAllItemRequests(user.getId(), PageRequest.of(0, 10));
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(ItemRequestResponseDto.class, response.get(0).getClass());
        assertEquals(itemRequest.getId(), response.get(0).getId());
        assertEquals(itemRequest.getDescription(), response.get(0).getDescription());
        assertEquals(itemRequest.getCreated(), response.get(0).getCreated());

        verify(itemRequestStorage, times(1)).findAllByOtherUsers(anyLong(), any());
    }

    @Test
    void testGetItemRequestById() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestStorage.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toItemRequestOutDto(any(), any())).thenReturn(itemRequestResponseDto);
        when(itemStorage.findAllByRequestId(anyLong())).thenReturn(null);

        ItemRequestResponseDto response = itemRequestService.createItemRequest(user.getId(), itemRequestDto);
        assertNotNull(response);
        assertEquals(ItemRequestResponseDto.class, response.getClass());
        assertEquals(itemRequest.getId(), response.getId());
        assertEquals(itemRequest.getDescription(), response.getDescription());
        assertEquals(itemRequest.getCreated(), response.getCreated());

        verify(itemRequestStorage, times(1)).save(any());
    }

}
