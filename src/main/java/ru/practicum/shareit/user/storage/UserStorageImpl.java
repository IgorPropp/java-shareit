package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Repository
public class UserStorageImpl implements UserStorage {

    private final List<User> users = Collections.synchronizedList(new ArrayList<>());
    private Long id = 1L;

    public List<UserDto> getAllUsers() {
        List<UserDto> list = new ArrayList<>();
        for (User user : users) {
            list.add(UserMapper.toDto(user));
        }
        log.info("List of all users requested");
        return list;
    }

    public UserDto getUser(Long userId) {
        for (User user : users) {
            if (user.getId().equals(userId)) {
                log.info("Found user with id=" + userId);
                return UserMapper.toDto(user);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    public UserDto createUser(UserDto userDto) {
        for (User user : users) {
            if (user.getEmail().equals(userDto.getEmail())) {
                throw new IllegalArgumentException("This email already exists");
            }
        }
        User user = UserMapper.fromDto(id, userDto);
        userDto.setId(id);
        id++;
        users.add(user);
        log.info("User created");
        return userDto;
    }

    public void deleteUser(Long id) throws IllegalAccessException {
        Iterator<User> userIterator = users.listIterator();
        while (userIterator.hasNext()) {
            User user = userIterator.next();
            if (user.getId().equals(id)) {
                userIterator.remove();
                log.info("User deleted");
                return;
            }
        }
        throw new IllegalAccessException("No user with this id");
    }

    public UserDto updateUser(Long id, UserDto updatedUserDto) throws IllegalAccessException {
        User updatedUser = UserMapper.fromDto(id, updatedUserDto);
        for (User user : users) {
            if (user.getId().equals(updatedUser.getId())) {
                if (user.getEmail().equals(updatedUser.getEmail())) {
                    log.info("User updated");
                    return UserMapper.toDto(user);
                }
                if (!emailExists(updatedUser.getEmail())) {
                    if (updatedUser.getEmail() != null) {
                        user.setEmail(updatedUser.getEmail());
                    }
                    if (updatedUser.getName() != null) {
                        user.setName(updatedUser.getName());
                    }
                    log.info("User updated");
                    return UserMapper.toDto(user);
                } else {
                    throw new IllegalAccessException("This email already exists");
                }
            }
        }
        throw new IllegalAccessException("No user with this id");
    }

    private boolean emailExists(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

}
