package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorIdOrderByCreatedAsc(Long userId);

    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedAsc(Long userId, Pageable page);
}
