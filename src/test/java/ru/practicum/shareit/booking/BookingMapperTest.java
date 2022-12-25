package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@SpringBootTest
public class BookingMapperTest {

    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void before() {
        User user = User
                .builder()
                .id(1L)
                .name("Habib")
                .email("habib@user.ru")
                .build();

        Item item = Item
                .builder()
                .id(1L)
                .name("Дрель")
                .description("дрель аккамуляторная")
                .available(true)
                .build();

        booking = Booking
                .builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        bookingDto = BookingMapper.toBookingDto(booking);
    }

    @Test
    void toBookingDto() {
        Assertions.assertNotNull(booking);
        Assertions.assertEquals(booking.getStart(), bookingDto.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        Assertions.assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        Assertions.assertEquals(booking.getStatus(), bookingDto.getStatus());
    }
}
