package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public List<UserDto> getAllUsers() {
        return userStorage.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getUser(Long id) {
        return userMapper.toDto(userStorage.findById(id).orElseThrow());
    }

    public User createUser(User user) {
        userStorage.save(user);
        return user;
    }

    public void deleteUser(Long id) {
        Optional<User> user = userStorage.findById(id);
        if (user.isPresent()) {
            userStorage.deleteById(id);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        Optional<User> user = userStorage.findById(id);
        if (user.isPresent()) {
            if (userDto.getEmail() != null) user.get().setEmail(userDto.getEmail());
            if (userDto.getName() != null) user.get().setName(userDto.getName());
            return userMapper.toDto(userStorage.save(user.get()));
        } else {
            throw new IllegalArgumentException();
        }
    }
}
