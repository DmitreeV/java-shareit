package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.BookingMapper.*;
import static ru.practicum.shareit.booking.model.Status.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingDto saveBooking(BookingRequestDto bookingDto, Long userId) {

        User user = getUser(userId);
        Item item = getItem(bookingDto.getItemId());

        if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Невозможно забронировать собственную вещь.");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь уже забронирована!");
        }
        validationData(bookingDto.getStart(), bookingDto.getEnd());

        Booking booking = toBooking(bookingDto, user, item);

        booking.setStatus(WAITING);
        log.info("Бронирование добавлено.");
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBooking(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Невозможно изменить бронирование.");
        }
        if (!booking.getStatus().equals(WAITING)) {
            throw new BadRequestException("Невозможно изменить статус бронирования.");
        }
        if (approved) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }
        log.info("Данные бронирования обновлены.");
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> getAllByBooker(Long userId, String state) {
        getUser(userId);

        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByIdDesc(userId, sort);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByIdDesc(userId, LocalDateTime.now(), sort);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByIdDesc(userId, LocalDateTime.now(), sort);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdDesc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByIdDesc(userId, WAITING, sort);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByIdDesc(userId, REJECTED, sort);
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
        return toBookingsDto(bookings);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Это не ваше бронирование.");
        }
        return toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, String state) {
        getUser(userId);

        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findBookingsByItem_Owner_IdOrderByStartDesc(userId, sort);
                break;
            case "PAST":
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), sort);
                break;
            case "FUTURE":
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), sort);
                break;
            case "CURRENT":
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case "WAITING":
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStatusEquals(userId, WAITING, sort);
                break;
            case "REJECTED":
                bookings = bookingRepository.findBookingsByItem_Owner_IdAndStatusEquals(userId, REJECTED, sort);
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
        return toBookingsDto(bookings);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Неверный ID пользователя."));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Неверный ID."));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование на найдено!."));
    }

    private void validationData(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new ValidationException("Неверные даты");
        }
    }
}

