package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@SpringBootTest
public class ItemMapperTest {

    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void before() {

        itemDto = ItemDto
                .builder()
                .id(1L)
                .name("Дрель")
                .description("дрель аккамуляторная")
                .available(true)
                .build();

        item = ItemMapper.toItem(itemDto);
    }

    @Test
    void toItem() {
        Assertions.assertNotNull(item);
        Assertions.assertEquals(item.getId(), itemDto.getId());
        Assertions.assertEquals(item.getName(), itemDto.getName());
        Assertions.assertEquals(item.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void toItemDto() {
        Assertions.assertNotNull(item);
        Assertions.assertEquals(ItemMapper.toItemDto(item).getId(), itemDto.getId());
        Assertions.assertEquals(ItemMapper.toItemDto(item).getName(), itemDto.getName());
        Assertions.assertEquals(ItemMapper.toItemDto(item).getDescription(), itemDto.getDescription());
        Assertions.assertEquals(ItemMapper.toItemDto(item).getAvailable(), itemDto.getAvailable());
    }
}
