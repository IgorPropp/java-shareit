package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Repository
public class UserStorageImpl implements UserStorage {

    private List<User> users = Collections.synchronizedList(new ArrayList<>());
    private Long id = 1L;

    public List<UserDto> getAllUsers() {
        List<UserDto> list = new ArrayList<>();
        for (User user : users) {
            list.add(UserMapper.toDto(user));
        }
        return list;
    }

    public UserDto getUser(Long userId) {
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return UserMapper.toDto(user);
            }
        }
        throw new IllegalArgumentException("Incorrect id");
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
        return userDto;
    }

    public void deleteUser(Long id) throws IllegalAccessException {
        Iterator<User> userIterator = users.listIterator();
        while (userIterator.hasNext()) {
            User user = userIterator.next();
            if (user.getId().equals(id)) {
                userIterator.remove();
                return;
            }
        }
        throw new IllegalAccessException("No user with this id");
    }

    public UserDto updateUser(Long id, UserDto updatedUser) throws IllegalAccessException {
        for (User user : users) {
            if (user.getEmail().equals(updatedUser.getEmail())) {
                if (!user.getId().equals(id)) {
                    throw new IllegalArgumentException("This email already exists");
                } else {
                    user.setEmail(updatedUser.getEmail());
                    if (updatedUser.getName() != null) {
                        user.setName(updatedUser.getName());
                    }
                    return UserMapper.toDto(user);
                }
            }
        }
        for (User user : users) {
            if (user.getId().equals(id)) {
                if (updatedUser.getEmail() != null) {
                    user.setEmail(updatedUser.getEmail());
                }
                if (updatedUser.getName() != null) {
                    user.setName(updatedUser.getName());
                }
                return UserMapper.toDto(user);
            }
        }
        throw new IllegalAccessException("No user with this id");
    }
}
