package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.List;

import static ru.practicum.shareit.user.UserMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        log.info("Получен список всех пользователей.");
        return toUsersDto(userRepository.getAllUsers());
    }

    public UserDto saveUser(UserDto userDto) {
        User user = toUser(userDto);
        validateUser(user);
        checkEmail(user);
        log.info("Пользователь сохранен.");
        return toUserDto(userRepository.saveUser(user));
    }

    public UserDto updateUser(UserDto userDto, int id) {
        User userToUpdate = userRepository.getUserById(id);
        User user = toUser(userDto);
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            checkEmail(user);
            userToUpdate.setEmail(user.getEmail());
        }
        log.info("Данные пользователя обновлены.");
        return toUserDto(userRepository.updateUser(userToUpdate));
    }

    @Override
    public UserDto getUserById(int id) {
        return toUserDto(userRepository.getUserById(id));
    }

    public void deleteUser(int id) {
        userRepository.deleteUser(id);
        log.info("Пользователь удален.");
    }

    private void checkEmail(User user) {
        for (User u : userRepository.getAllUsers()) {
            if (user.getEmail().equals(u.getEmail())) {
                throw new RuntimeException("Пользователь с таким адресом электронной почты уже существует.");
            }
        }
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@") ||
        user.getName().isBlank()) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
    }
}
