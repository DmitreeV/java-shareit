package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
//import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.model.Status.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceImplTest {

    private final EntityManager em;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final LocalDateTime timestamp1 = LocalDateTime.of(2022, 11, 20, 10, 30);
    private final LocalDateTime timestamp2 = LocalDateTime.of(2022, 11, 22, 11, 30);
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void init() {

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Oleg")
                .email("oleg@email.com")
                .build();
        userService.saveUser(userDto);

        UserDto userDto1 = UserDto.builder()
                .id(2L)
                .name("Roman")
                .email("roman@email.com")
                .build();
        userService.saveUser(userDto1);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();
        itemService.saveItem(itemDto, userDto.getId());

        bookingRequestDto = BookingRequestDto
                .builder()
                .start(timestamp1)
                .end(timestamp2)
                .itemId(1L)
                .build();
    }

    @Test
    void testSaveBooking() {

        bookingService.saveBooking(bookingRequestDto, 2L);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.id = :id", Booking.class);
        Booking booking = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(booking.getStart(), equalTo(timestamp1));
        assertThat(booking.getEnd(), equalTo(timestamp2));
        assertThat(booking.getStatus(), equalTo(WAITING));
    }

    @Test
    void testSaveBookingFailOwnerBooking() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.saveBooking(bookingRequestDto, 1L));
        assertThat(e.getMessage(), equalTo("Невозможно забронировать собственную вещь."));
    }

    @Test
    void testSaveBookingFailUserNotFound() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.saveBooking(bookingRequestDto, 50L));
        assertThat(e.getMessage(), equalTo("Неверный ID пользователя."));
    }

    @Test
    void testSaveBookingFailItemNotFound() {
        bookingRequestDto.setItemId(50L);
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.saveBooking(bookingRequestDto, 2L));
        assertThat(e.getMessage(), equalTo("Неверный ID."));
    }

//    @Test
//    void testSaveBookingValidationData() {
//        bookingRequestDto.setStart(timestamp2);
//        bookingRequestDto.setEnd(timestamp1);
//        ValidationException e = assertThrows(ValidationException.class,
//                () -> bookingService.saveBooking(bookingRequestDto, 2L));
//
//        assertThat(e.getMessage(), equalTo("Неверные даты"));
//    }

    @Test
    void testUpdateBookingFailBookingNotFound() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.updateBooking(1L, 20L, true));
        assertThat(e.getMessage(), equalTo("Бронирование на найдено!."));
    }

    @Test
    void testGetById() {
        bookingService.saveBooking(bookingRequestDto, 2L);
        BookingDto booking1 = bookingService.getById(2L, 1L);

        assertThat(booking1.getStatus(), equalTo(WAITING));
    }

    @Test
    void testGetByIdFailBookingNotFound() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getById(2L, 100L));
        assertThat(e.getMessage(), equalTo("Бронирование на найдено!."));
    }

    @Test
    void testGetByIdFailWrongRequest() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getById(3L, 1L));
        assertThat(e.getMessage(), equalTo("Это не ваше бронирование."));
    }

    @Test
    void testGetAllByBookerByStateStatus() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        List<BookingDto> bookings = bookingService.getAllByBooker(2L, "WAITING", 0, 2);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void testGetAllByBookerByStatePast() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        List<BookingDto> bookings = bookingService.getAllByBooker(2L, "PAST", 0, 2);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void testGetAllByBookerByStateRejected() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        List<BookingDto> bookings = bookingService.getAllByBooker(2L, "REJECTED", 0, 2);

        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void testGetAllByBookerByStateFuture() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        List<BookingDto> bookings = bookingService.getAllByBooker(2L, "FUTURE", 0, 2);

        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void testGetAllByBookerByStateCurrent() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        List<BookingDto> bookings = bookingService.getAllByBooker(2L, "CURRENT", 0, 2);

        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void testGetAllByBookerByStateAll() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        List<BookingDto> bookings = bookingService.getAllByBooker(2L, "ALL", 0, 2);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void testGetAllByBookerFailByWrongState() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        BadRequestException e = assertThrows(BadRequestException.class,
                () -> bookingService.getAllByBooker(2L, "FUTUR", 0, 2));
        assertThat(e.getMessage(), equalTo("Unknown state: FUTUR"));
    }

    @Test
    void testGetAllByBookerFailWrongUser() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getAllByBooker(10L, "ALL", 0, 2));
        assertThat(e.getMessage(), equalTo("Неверный ID пользователя."));
    }

    @Test
    void testGetAllByOwnerByStateStatus() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        List<BookingDto> bookings = bookingService.getAllByOwner(1L, "WAITING", 0, 2);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void testGetAllByOwnerByStatePast() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        List<BookingDto> bookings = bookingService.getAllByOwner(1L, "PAST", 0, 2);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void testGetAllByOwnerByStateRejected() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        List<BookingDto> bookings = bookingService.getAllByOwner(1L, "REJECTED", 0, 2);

        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void testGetAllByOwnerByStateFuture() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        List<BookingDto> bookings = bookingService.getAllByOwner(1L, "FUTURE", 0, 2);

        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void testGetAllByOwnerByStateCurrent() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        List<BookingDto> bookings = bookingService.getAllByOwner(1L, "CURRENT", 0, 2);

        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void testGetAllByOwnerByStateAll() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        List<BookingDto> bookings = bookingService.getAllByOwner(1L, "ALL", 0, 2);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void testGetAllByOwnerFailByWrongState() {
        bookingService.saveBooking(bookingRequestDto, 2L);

        BadRequestException e = assertThrows(BadRequestException.class,
                () -> bookingService.getAllByOwner(2L, "FUTUR", 0, 2));
        assertThat(e.getMessage(), equalTo("Unknown state: FUTUR"));
    }

    @Test
    void testGetAllByOwnerFailWrongUser() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getAllByOwner(10L, "ALL", 0, 2));
        assertThat(e.getMessage(), equalTo("Неверный ID пользователя."));
    }
}
