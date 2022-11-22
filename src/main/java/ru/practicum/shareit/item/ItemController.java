package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemDto itemDto) {
        return itemService.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId,
                       @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    ItemDto getItemById(@PathVariable int itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    List<ItemDto> getItemByUserId(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItemByUserId(userId);
    }

    @GetMapping("/search")
    List<ItemDto> getItemByText(@RequestParam String text) {
        return itemService.getItemByText(text);
    }
}
