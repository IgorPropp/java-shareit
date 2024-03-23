package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto updateUser) throws IllegalAccessException;

    void deleteUser(Long id) throws IllegalAccessException;

    List<UserDto> getAllUsers();

    UserDto getUser(Long userId);

}
