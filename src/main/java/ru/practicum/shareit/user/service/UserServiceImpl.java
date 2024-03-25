package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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

    public List<UserDto> getAllUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getUser(Long id) {
        return UserMapper.toDto(userStorage.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
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
            return UserMapper.toDto(userStorage.save(user.get()));
        } else {
            throw new IllegalArgumentException();
        }
    }
}
