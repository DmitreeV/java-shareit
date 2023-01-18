package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto saveItemRequest(ItemRequestDto itemRequestDto, Long userId);

    ItemRequestDto getRequestById(Long requestId, Long userId);

    List<ItemRequestDto> getAllByUserId(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId, int from, int size);
}
