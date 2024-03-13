package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@AllArgsConstructor
class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public UserDto getUser(Long id) {
        return userStorage.getUser(id);
    }

    public UserDto createUser(UserDto userDto) {
        return this.userStorage.createUser(userDto);
    }

    public void deleteUser(Long id) throws IllegalAccessException {
        userStorage.deleteUser(id);
    }

    public UserDto updateUser(Long id, UserDto userDto) throws IllegalAccessException {
        return this.userStorage.updateUser(id, userDto);
    }
}
