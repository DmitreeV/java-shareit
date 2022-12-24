package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto saveBooking(BookingRequestDto bookingDto, Long userId);

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);

    List<BookingDto> getAllByBooker(Long userId, String state, int from, int size);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllByOwner(Long userId, String state, int from, int size);
}
