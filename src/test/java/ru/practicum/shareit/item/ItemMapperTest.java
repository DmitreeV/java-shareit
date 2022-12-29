package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

@SpringBootTest
public class ItemMapperTest {

    private Item item;
    private ItemDto itemDto;
    private ItemShortDto itemShortDto;

    @BeforeEach
    void before() {

        ItemRequest itemRequest = ItemRequest
                .builder()
                .id(1L)
                .description("description")
                .build();

        item = Item
                .builder()
                .id(1L)
                .name("Дрель")
                .description("дрель аккамуляторная")
                .available(true)
                .request(itemRequest)
                .build();

        itemDto = ItemMapper.toItemDto(item);
        itemShortDto = ItemMapper.toItemShortDto(item);

    }

    @Test
    void toItemDto() {
        Assertions.assertNotNull(item);
        Assertions.assertEquals(item.getId(), itemDto.getId());
        Assertions.assertEquals(item.getName(), itemDto.getName());
        Assertions.assertEquals(item.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void toItemShortDto() {
        Assertions.assertNotNull(item);
        Assertions.assertEquals(item.getId(), itemShortDto.getId());
        Assertions.assertEquals(item.getName(), itemShortDto.getName());
        Assertions.assertEquals(item.getDescription(), itemShortDto.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemShortDto.getAvailable());
    }
}
