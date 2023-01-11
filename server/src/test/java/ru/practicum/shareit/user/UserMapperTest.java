package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
public class UserMapperTest {

    private User user;
    private UserDto userDto;

    @BeforeEach
    void before() {
        user = User
                .builder()
                .id(1L)
                .name("Habib")
                .email("habib@user.ru")
                .build();
        userDto = UserMapper.toUserDto(user);
    }

    @Test
    void toUserDto() {
        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(user.getId(), userDto.getId());
        Assertions.assertEquals(user.getName(), userDto.getName());
        Assertions.assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void toUser() {
        Assertions.assertNotNull(user);
        Assertions.assertEquals(UserMapper.toUserDto(user).getId(), userDto.getId());
        Assertions.assertEquals(UserMapper.toUserDto(user).getName(), userDto.getName());
        Assertions.assertEquals(UserMapper.toUserDto(user).getEmail(), userDto.getEmail());
    }
}
