package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) throws IllegalAccessException {
        userService.deleteUser(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) throws IllegalAccessException {
        return userService.updateUser(userId, userDto);
    }
}
