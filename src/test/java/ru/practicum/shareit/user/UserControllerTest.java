package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureWebMvc
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService service;

    @Autowired
    private MockMvc mvc;

    private final UserDto dto1 = new UserDto(1L, "Ivan", "ivan@yandex.ru");
    private final UserDto dto2 = new UserDto(2L, "Pasha", "pasha@yandex.ru");

    @Test
    void testSaveUserWithStatus200() throws Exception {
        when(service.saveUser(any())).thenReturn(dto1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto1))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto1.getName())))
                .andExpect(jsonPath("$.email", is(dto1.getEmail())));

        verify(service, times(1)).saveUser(any());
    }

    @Test
    void testSaveUserWithoutNameWithStatus400() throws Exception {
        UserDto userDto = new UserDto(3L, null, "ivan@yandex.ru");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void testSaveUserWithoutEmailWithStatus400() throws Exception {
        UserDto userDto = new UserDto(3L, "Ivan", null);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void testSaveUserWithWrongEmailWithStatus400() throws Exception {
        UserDto userDto = new UserDto(3L, "Ivan", "badEmail%yandex.ru");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void testUpdateUserWithStatus200() throws Exception {
        dto1.setName("Anton");
        when(service.updateUser(any(), any())).thenReturn(dto1);

        mvc.perform(patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(dto1))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto1.getName())))
                .andExpect(jsonPath("$.email", is(dto1.getEmail())));
    }

    @Test
    void testGetUserByIdAndStatus200() throws Exception {
        when(service.getUserById(1L))
                .thenReturn(dto1);

        mvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto1.getName())))
                .andExpect(jsonPath("$.email", is(dto1.getEmail())));

        verify(service, times(1)).getUserById(1L);
    }

    @Test
    void testGetUserByIdAndStatus404() throws Exception {
        when(service.getUserById(20L))
                .thenThrow(new NotFoundException("Пользователь не найден!"));

        mvc.perform(get("/users/{id}", 20))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Пользователь не найден!")));

        verify(service, times(1)).getUserById(20L);
    }

    @Test
    void testGetAllUsersAndStatus200() throws Exception {
        when(service.getAllUsers())
                .thenReturn(List.of(dto1, dto2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(dto1.getName())))
                .andExpect(jsonPath("$[0].email", is(dto1.getEmail())))
                .andExpect(jsonPath("$[1].name", is(dto2.getName())))
                .andExpect(jsonPath("$[1].email", is(dto2.getEmail())));

        verify(service, times(1)).getAllUsers();
    }

    @Test
    void testDeleteUserAndStatus200() throws Exception {
        mvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());

        verify(service, times(1)).deleteUser(1L);
    }
}
