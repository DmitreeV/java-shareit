package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplTest {
    private final EntityManager em;
    private final UserService service;

    @Test
    void testIntegrationSaveUser() {

        UserDto userDto = new UserDto(1L, "Petr", "petr@yandex.ru");
        service.saveUser(userDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }
    
    @Test
    void testSaveUser() {

        UserDto userDto = new UserDto(1L, "Petr", "petr@yandex.ru");
        service.saveUser(userDto);

        assertThat(1L, equalTo(userDto.getId()));
        assertThat("Petr", equalTo(userDto.getName()));
        assertThat("petr@yandex.ru", equalTo(userDto.getEmail()));
    }

    @Test
    void testUpdateUser() {

        UserDto userDto = new UserDto(1L, "Ivan", "ivan@yandex.ru");
        service.saveUser(userDto);

        userDto.setName("Vanya");
        userDto.setEmail("vanya@yandex.ru");
        service.updateUser(userDto, 1L);

        assertThat("Vanya", equalTo(userDto.getName()));
        assertThat("vanya@yandex.ru", equalTo(userDto.getEmail()));
    }

    @Test
    void testUpdateUserFail() {
        UserDto userDto = new UserDto(1L, "Ivan", "ivan@yandex.ru");
        service.saveUser(userDto);

        NotFoundException e = assertThrows(NotFoundException.class, () -> service.updateUser(userDto, 66L));
        assertThat(e.getMessage(), equalTo("Неверный ID пользователя."));
    }

    @Test
    void testGetUserById() {

        UserDto userDto = new UserDto(1L, "Oleg", "oleg@yandex.ru");
        service.saveUser(userDto);
        UserDto userDto1 = new UserDto(2L, "Roma", "roma@yandex.ru");
        service.saveUser(userDto1);

        UserDto userDto2 = service.getUserById(1L);
        UserDto userDto3 = service.getUserById(2L);

        assertThat("Oleg", equalTo(userDto2.getName()));
        assertThat("Roma", equalTo(userDto3.getName()));
    }

    @Test
    void testGetUserByIdFail() {

        NotFoundException e = assertThrows(NotFoundException.class, () -> service.getUserById(55L));
        assertThat(e.getMessage(), equalTo("Неверный ID пользователя."));
    }

    @Test
    void testDeleteUser() {

        UserDto userDto = new UserDto(1L, "Oleg", "oleg@yandex.ru");
        service.saveUser(userDto);
        UserDto userDto1 = new UserDto(2L, "Roma", "roma@yandex.ru");
        service.saveUser(userDto1);

        service.deleteUser(2L);

        assertThat(1, equalTo(service.getAllUsers().size()));
    }

    @Test
    void testGetAllUsers() {

        UserDto userDto = new UserDto(1L, "Oleg", "oleg@yandex.ru");
        service.saveUser(userDto);
        UserDto userDto1 = new UserDto(2L, "Roma", "roma@yandex.ru");
        service.saveUser(userDto1);
        UserDto userDto2 = new UserDto(3L, "Bob", "bob@yandex.ru");
        service.saveUser(userDto2);

        List<UserDto> users = service.getAllUsers();

        System.out.println(users.get(0));
        System.out.println(users.get(1));
        System.out.println(users.get(2));

        assertThat(3, equalTo(users.size()));
    }
}
