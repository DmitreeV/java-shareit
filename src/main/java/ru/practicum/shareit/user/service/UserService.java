package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto saveUser(UserDto user);

    UserDto updateUser(UserDto user, int id);

    UserDto getUserById(int id);

    void deleteUser(int id);
}
