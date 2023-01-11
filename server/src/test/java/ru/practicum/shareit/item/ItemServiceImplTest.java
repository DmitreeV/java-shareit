package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ValidationException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceImplTest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final CommentService commentService;

    private final UserDto userDto = UserDto
            .builder()
            .id(1L)
            .name("Petr")
            .email("petr@yandex.ru")
            .build();

    private final ItemDto itemDto = ItemDto
            .builder()
            .id(1L)
            .name("Молоток")
            .description("молоток забивной")
            .available(true)
            .build();

    private final CommentDto commentDto = CommentDto
            .builder()
            .id(1L)
            .text("новый комментарий")
            .build();

    @Test
    void testSaveItem() {

        userService.saveUser(userDto);
        itemService.saveItem(itemDto, 1L);

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.name = :name", Item.class);
        Item checkedItem = query
                .setParameter("name", itemDto.getName())
                .getSingleResult();

        assertThat(checkedItem.getId(), equalTo(1L));
        assertThat(checkedItem.getName(), equalTo(itemDto.getName()));
        assertThat(checkedItem.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void testSaveItemFailWrongUser() {

        userService.saveUser(userDto);
        itemService.saveItem(itemDto, 1L);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.saveItem(itemDto, 20L));
        assertThat(e.getMessage(), equalTo("Неверный ID пользователя."));
    }

    @Test
    void testSaveItemFailWrongItemRequest() {

        userService.saveUser(userDto);
        itemService.saveItem(itemDto, 1L);
        itemDto.setRequestId(20L);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.saveItem(itemDto, 1L));
        assertThat(e.getMessage(), equalTo("Неверный ID запроса."));
    }

    @Test
    void testSaveItemFailWrongDescription() {

        userService.saveUser(userDto);
        itemService.saveItem(itemDto, 1L);
        itemDto.setDescription(null);

        ValidationException e = assertThrows(ValidationException.class,
                () -> itemService.saveItem(itemDto, 1L));

        assertThat(e.getMessage(), equalTo("Неверные данные."));
    }

    @Test
    void testSaveItemFailWrongAvailable() {

        userService.saveUser(userDto);
        itemService.saveItem(itemDto, 1L);
        itemDto.setAvailable(null);

        ValidationException e = assertThrows(ValidationException.class,
                () -> itemService.saveItem(itemDto, 1L));

        assertThat(e.getMessage(), equalTo("Неверные данные."));
    }

    @Test
    void testUpdateItem() {

        userService.saveUser(userDto);
        itemService.saveItem(itemDto, 1L);

        itemDto.setName("Кувалда");
        itemDto.setDescription("надежней чем молоток");
        itemService.updateItem(1L, itemDto, 1L);

        assertThat("Кувалда", equalTo(itemDto.getName()));
        assertThat("надежней чем молоток", equalTo(itemDto.getDescription()));
    }

    @Test
    void testUpdateItemFailWrongUser() {
        userService.saveUser(userDto);
        itemService.saveItem(itemDto, 1L);

        itemDto.setId(1L);
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, itemDto, 20L));
        assertThat(e.getMessage(), equalTo("Неверный ID пользователя."));
    }

    @Test
    void testUpdateItemFailWrongItem() {
        userService.saveUser(userDto);
        itemService.saveItem(itemDto, 1L);

        itemDto.setId(50L);
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(50L, itemDto, 1L));
        assertThat(e.getMessage(), equalTo("Неверный ID."));
    }

    @Test
    void testGetItemByIdNoBookings() {
        userService.saveUser(userDto);
        itemService.saveItem(itemDto, 1L);
        ItemDto item = itemService.getItemById(1L, 1L);

        assertThat(item.getName(), equalTo("Молоток"));
        assertThat(item.getLastBooking(), nullValue());
        assertThat(item.getNextBooking(), nullValue());
    }

    @Test
    void testGetItemByUserId() {
        userService.saveUser(userDto);
        itemService.saveItem(itemDto, 1L);

        List<ItemDto> items = itemService.getItemByUserId(1L, 0, 2);
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getName(), equalTo("Молоток"));
    }

    @Test
    void testGetItemByIdFailWrongItem() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.getItemById(20L, 1L));
        assertThat(e.getMessage(), equalTo("Неверный ID."));
    }

    @Test
    void testGetItemByText() {
        userService.saveUser(userDto);
        itemService.saveItem(itemDto, 1L);

        List<ItemDto> searched = itemService.getItemByText("МолоТ", 0, 3);

        assertThat(searched.size(), equalTo(1));
        assertThat(searched.get(0).getDescription(), equalTo("молоток забивной"));
    }

    @Test
    void testSaveCommentFailWrongItem() {
        userService.saveUser(userDto);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> commentService.saveComment(20L, 1L, commentDto));

        assertThat(e.getMessage(), equalTo("Неверный ID."));
    }

    @Test
    void testSaveCommentFailWrongUser() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> commentService.saveComment(1L, 20L, commentDto));

        assertThat(e.getMessage(), equalTo("Неверный ID пользователя."));
    }
}
