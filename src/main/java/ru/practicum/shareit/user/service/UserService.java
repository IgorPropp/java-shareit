package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUser(Long id);

    UserDto createUser(UserDto userDto);

    void deleteUser(Long id) throws IllegalAccessException;

    UserDto updateUser(Long id, UserDto userDto) throws IllegalAccessException;

}
