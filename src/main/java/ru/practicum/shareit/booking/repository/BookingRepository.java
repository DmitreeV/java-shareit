package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    //ALL
    Page<Booking> findAllByBookerIdOrderByIdDesc(Long userId, Pageable page);

    //PAST
    Page<Booking> findAllByBookerIdAndEndBeforeOrderByIdDesc(Long userId, LocalDateTime now, Pageable page);

    //FUTURE
    Page<Booking> findAllByBookerIdAndStartAfterOrderByIdDesc(Long userId, LocalDateTime now, Pageable page);

    //CURRENT
    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdDesc(Long userId, LocalDateTime now,
                                                                          LocalDateTime now1, Pageable page);

    //WAITING
    //REJECTED
    Page<Booking> findAllByBookerIdAndStatusOrderByIdDesc(Long userId, Status status, Pageable page);

    //BY OWNER
    //All
    Page<Booking> findBookingsByItem_Owner_IdOrderByStartDesc(Long bookerId, Pageable page);

    //PAST
    Page<Booking> findBookingsByItem_Owner_IdAndEndIsBefore(Long bookerId, LocalDateTime now, Pageable page);

    //FUTURE
    Page<Booking> findBookingsByItem_Owner_IdAndStartIsAfter(Long bookerId, LocalDateTime now, Pageable page);

    //CURRENT
    Page<Booking> findBookingsByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime now,
                                                                           LocalDateTime now1, Pageable page);

    //WAITING
    //REJECTED
    Page<Booking> findBookingsByItem_Owner_IdAndStatusEquals(Long bookerId, Status status, Pageable page);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long userId, Long itemId, Status approved,
                                                                          LocalDateTime now);

    List<Booking> findBookingsByItem(Item item);
}
