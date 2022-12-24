package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void init() {

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Oleg")
                .email("oleg@email.com")
                .build();
        userService.saveUser(userDto);

        itemRequestDto = ItemRequestDto
                .builder()
                .id(1L)
                .description("описание запроса")
                .build();
    }

    @Test
    void testIntegrationSaveItemRequest() {
        itemRequestService.saveItemRequest(itemRequestDto, 1L);

        TypedQuery<ItemRequest> query = em
                .createQuery("SELECT i FROM ItemRequest i WHERE i.description = :description", ItemRequest.class);
        ItemRequest checkedRequest = query
                .setParameter("description", itemRequestDto.getDescription())
                .getSingleResult();

        assertThat(checkedRequest.getId(), equalTo(1L));
        assertThat(checkedRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    void testSaveItemRequestFailUserNotFound() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.saveItemRequest(itemRequestDto, 20L));
        assertThat(e.getMessage(), equalTo("Неверный ID пользователя."));
    }

    @Test
    void testGetRequestById() {
        itemRequestService.saveItemRequest(itemRequestDto, 1L);

        ItemRequestDto request = itemRequestService.getRequestById(1L, 1L);

        assertThat(request.getDescription(), equalTo("описание запроса"));
        assertThat(request.getId(), equalTo(1L));
    }

    @Test
    void testGetRequestByIdFailWrongUser() {
        itemRequestService.saveItemRequest(itemRequestDto, 1L);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 20L));
        assertThat(e.getMessage(), equalTo("Неверный ID пользователя."));
    }

    @Test
    void testGetRequestByIdFailWrongRequest() {
        itemRequestService.saveItemRequest(itemRequestDto, 1L);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(20L, 1L));
        assertThat(e.getMessage(), equalTo("Неверный ID запроса."));
    }

    @Test
    void testGetAllByUserId() {
        List<ItemRequestDto> requests = itemRequestService.getAllByUserId(1L);

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getDescription(), equalTo("описание запроса"));
    }

    @Test
    void testGetAllByUserIdFailUserNotFound() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllByUserId(20L));

        assertThat(e.getMessage(), equalTo("Неверный ID пользователя."));
    }

    @Test
    void testGetAllRequests() {

        UserDto userDto = UserDto.builder()
                .id(2L)
                .name("Ivan")
                .email("ivan@email.com")
                .build();
        userService.saveUser(userDto);

        ItemRequestDto itemRequestDto = ItemRequestDto
                .builder()
                .id(2L)
                .description("запрос")
                .build();
        itemRequestService.saveItemRequest(itemRequestDto, 2L);

        List<ItemRequestDto> requests = itemRequestService.getAllRequests(1L, 0, 2);

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getDescription(), equalTo("запрос"));
    }
}
