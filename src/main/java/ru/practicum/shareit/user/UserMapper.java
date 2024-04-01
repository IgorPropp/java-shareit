package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {

    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public User fromDto(Long userId, UserDto userDto) {
        return new User(
                userId,
                userDto.getName(),
                userDto.getEmail()
        );
    }
}
