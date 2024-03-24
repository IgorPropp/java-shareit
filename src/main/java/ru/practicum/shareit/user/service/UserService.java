package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUser(Long id);

    User createUser(User user);

    void deleteUser(Long id) throws IllegalAccessException;

    UserDto updateUser(Long id, UserDto userDto) throws IllegalAccessException;

}
