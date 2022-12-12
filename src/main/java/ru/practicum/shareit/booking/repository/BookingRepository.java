package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    //ALL
    List<Booking> findAllByBookerIdOrderByIdDesc(Long userId, Sort sort);

    //PAST
    List<Booking> findAllByBookerIdAndEndBeforeOrderByIdDesc(Long userId, LocalDateTime now, Sort sort);

    //FUTURE
    List<Booking> findAllByBookerIdAndStartAfterOrderByIdDesc(Long userId, LocalDateTime now, Sort sort);

    //CURRENT
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdDesc(Long userId, LocalDateTime now,
                                                                          LocalDateTime now1, Sort sort);

    //WAITING
    //REJECTED
    List<Booking> findAllByBookerIdAndStatusOrderByIdDesc(Long userId, Status status, Sort sort);

    //BY OWNER
    //All
    List<Booking> findBookingsByItem_Owner_IdOrderByStartDesc(Long bookerId, Sort sort);

    //PAST
    List<Booking> findBookingsByItem_Owner_IdAndEndIsBefore(Long bookerId, LocalDateTime now, Sort sort);

    //FUTURE
    List<Booking> findBookingsByItem_Owner_IdAndStartIsAfter(Long bookerId, LocalDateTime now, Sort sort);

    //CURRENT
    List<Booking> findBookingsByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime now,
                                                                      LocalDateTime now1, Sort sort);

    //WAITING
    //REJECTED
    List<Booking> findBookingsByItem_Owner_IdAndStatusEquals(Long bookerId, Status status, Sort sort);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long userId, Long itemId, Status approved,
                                                                          LocalDateTime now);
}
