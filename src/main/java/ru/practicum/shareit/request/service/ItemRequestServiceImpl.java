package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.ItemRequestMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto saveItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = getUser(userId);
        ItemRequest itemRequest = toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        log.info("Запрос добавлен.");
        return toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        getUser(userId);

        ItemRequest itemRequest = getItemRequest(requestId);
        ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);
        setItemsToItemRequestDto(itemRequestDto);
        log.info("Получены данные о запросе.");
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllByUserId(Long userId) {
        getUser(userId);
        List<ItemRequestDto> requests =
                toItemRequestsDto(itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(userId));

        requests.forEach(this::setItemsToItemRequestDto);
        log.info("Получен список запросов пользователя.");
        return requests;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        User user = getUser(userId);

        Pageable page = PageRequest.of(from / size, size);
        List<ItemRequestDto> requests =
                toItemRequestsDto(itemRequestRepository.findAllByRequestorNotLikeOrderByCreatedAsc(user, page));

        requests.forEach(this::setItemsToItemRequestDto);
        log.info("Получен список всех запросов, созданных другими пользователями.");
        return requests;
    }

    private void setItemsToItemRequestDto(ItemRequestDto itemRequestDto) {
        itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId())
                .stream()
                .map(ItemMapper::toItemShortDto)
                .collect(Collectors.toList()));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Неверный ID пользователя."));
    }

    private ItemRequest getItemRequest(Long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Неверный ID запроса."));
    }
}
