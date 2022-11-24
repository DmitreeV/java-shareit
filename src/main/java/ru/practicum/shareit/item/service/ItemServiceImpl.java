package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto saveItem(ItemDto itemDto, int userId) {
        User userOwner = userRepository.getUserById(userId);
        Item item = toItem(itemDto, userOwner);
        validateItem(item);
        log.info("Вещь добавлена.");
        return toItemDto(itemRepository.saveItem(item));
    }

    @Override
    public ItemDto updateItem(int itemId, ItemDto itemDto, Integer userId) {
        Item item = itemRepository.getItemById(itemId);
        if (!userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Неверный ID пользователя.");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        log.info("Данные вещи обновлены.");
        return toItemDto(itemRepository.updateItem(item));
    }

    @Override
    public ItemDto getItemById(int itemId) {
        return toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemByUserId(int userId) {
        log.info("Получен список всех вещей пользователя.");
        return itemRepository.getAllItems().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        String query = text.toLowerCase();
        List<Item> items = itemRepository.getItemByText(query);
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        return toItemsDto(items);
    }

    private void validateItem(Item item) {
        if (item.getAvailable() == null || item.getName().isBlank() || item.getDescription() == null ||
        item.getDescription().isBlank() || item.getName() == null) {
            throw new ValidationException("Неверные данные.");
        }
    }
}
