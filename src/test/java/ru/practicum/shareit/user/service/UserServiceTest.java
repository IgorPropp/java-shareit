package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    UserStorage userStorage;
    @Mock
    UserMapper userMapper;
    @InjectMocks
    UserServiceImpl userService;
    User user;
    UserDto userDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "userName", "user@email.com");
        userDto = new UserDto(1L, "userName", "user@email.com");
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUser() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toDto(any())).thenReturn(userDto);

        UserDto response = userService.getUser(user.getId());

        assertEquals(userDto, response);

        verify(userStorage, times(1)).findById(anyLong());
    }

    @Test
    void testGetAllUsers() {
        when(userStorage.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(any())).thenReturn(userDto);

        List<UserDto> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(userDto, users.get(0));

        verify(userStorage, times(1)).findAll();
    }

    @Test
    void testCreateUser() {
        when(userStorage.save(any())).thenReturn(user);
        User user = userService.createUser(new User(1L, "user Name", "user@email.ru"));

        assertThat(user.getId(), equalTo(user.getId()));
        assertThat(user.getName(), equalTo(user.getName()));
        assertThat(user.getEmail(), equalTo(user.getEmail()));

        verify(userStorage, times(1)).save(any());
    }

    @Test
    void testUpdateUser() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(userStorage.save(any())).thenReturn(user);

        UserDto updatedUser = userService.updateUser(user.getId(), userDto);

        assertNotNull(updatedUser);
        assertThat(updatedUser.getId(), equalTo(user.getId()));
        assertThat(updatedUser.getName(), equalTo(user.getName()));
        assertThat(updatedUser.getEmail(), equalTo(user.getEmail()));

        verify(userStorage, times(1)).save(any());
    }

    @Test
    void testDeleteUser() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.deleteUser(user.getId()));

        verify(userStorage, times(1)).deleteById(anyLong());
    }
}
