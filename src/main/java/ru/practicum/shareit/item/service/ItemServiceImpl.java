package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.ItemMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        User savedUser = getUser(userId);
        Item item = toItem(itemDto);
        item.setOwner(savedUser);
        validateItem(item);
        log.info("Вещь добавлена.");
        return toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {

        Item item = getItem(itemId);
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
        return toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {

        Item item = getItem(itemId);
        ItemDto itemDto = toItemDto(item);
        populateItemDto(itemDto);
        if (!item.getOwner().getId().equals(userId)) {
            itemDto.setLastBooking(null);
            itemDto.setNextBooking(null);
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemByUserId(Long userId) {

        List<Item> items = itemRepository.findAllByOwnerId(userId).stream()
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());

        List<ItemDto> itemsDto = this.setBookings(items);
        this.setComments(itemsDto);
        log.info("Получен список всех вещей пользователя.");
        return itemsDto;
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

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Неверный ID пользователя."));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Неверный ID."));
    }

    private void validateItem(Item item) {
        if (item.getAvailable() == null || item.getName().isBlank() || item.getDescription() == null ||
                item.getDescription().isBlank() || item.getName() == null) {
            throw new ValidationException("Неверные данные.");
        }
    }

    private void populateItemDto(ItemDto itemDto) {
        BookingShortDto lastBooking = getLastBooking(itemDto.getId());
        BookingShortDto nextBooking = getNextBooking(itemDto.getId());
        List<CommentDto> comments = commentRepository.findAllByItemId(itemDto.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(comments);
    }

    private BookingShortDto getNextBooking(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now())
                .map(BookingMapper::toBookingShortDto)
                .orElse(null);
    }

    private BookingShortDto getLastBooking(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(itemId, LocalDateTime.now())
                .map(BookingMapper::toBookingShortDto)
                .orElse(null);
    }

    private void setComments(List<ItemDto> items) {
        Map<Long, ItemDto> itemsDto = new HashMap<>();
        items.forEach(item -> itemsDto.put(item.getId(), item));

        Set<Comment> comments = new HashSet<>(commentRepository.findCommentsByItemId(itemsDto.keySet()));

        if (!itemsDto.isEmpty()) {
            comments.forEach(comment -> Optional.ofNullable(itemsDto.get(comment.getItem().getId()))
                    .ifPresent(i -> i.getComments().add(toCommentDto(comment))));
        }
    }

    private List<ItemDto> setBookings(List<Item> items) {
        List<ItemDto> itemsDto = toItemsDto(items);

        if (!itemsDto.isEmpty()) {
            itemsDto.forEach(item -> {
                item.setNextBooking(getNextBooking(item.getId()));
                item.setLastBooking(getLastBooking(item.getId()));

            });
        }
        return itemsDto;
    }
}
