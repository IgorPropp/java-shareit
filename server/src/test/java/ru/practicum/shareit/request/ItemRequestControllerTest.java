package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    User user;
    ItemRequestResponseDto itemRequestResponseDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "userName", "user@email.ru");
        itemRequestResponseDto = new ItemRequestResponseDto(1L,"Request Description",
                LocalDateTime.of(2024, 10, 10, 10, 10, 10), Collections.emptyList());
    }

    @Test
    void testCreateItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any()))
                .thenReturn(itemRequestResponseDto);
        ItemRequestDto itemRequestDto = new ItemRequestDto(itemRequestResponseDto.getId(),
                itemRequestResponseDto.getDescription(), user,
                itemRequestResponseDto.getCreated());

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestResponseDto.getCreated().toString())));

        verify(itemRequestService, times(1)).createItemRequest(anyLong(), any());
    }

    @Test
    void testGetRequestsByOwner() throws Exception {
        when(itemRequestService.getItemRequestByOwner(anyLong()))
                .thenReturn(List.of(itemRequestResponseDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestResponseDto))));

        verify(itemRequestService, times(1)).getItemRequestByOwner(anyLong());
    }

    @Test
    void testGetAllItemRequests() throws Exception {
        when(itemRequestService.getAllItemRequests(anyLong(), any()))
                .thenReturn(List.of(itemRequestResponseDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestResponseDto))));
        verify(itemRequestService, times(1)).getAllItemRequests(anyLong(), any());
    }

    @Test
    void testGetItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestResponseDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(itemRequestResponseDto.getCreated().toString()), String.class));

        verify(itemRequestService, times(1)).getItemRequestById(anyLong(), anyLong());
    }
}