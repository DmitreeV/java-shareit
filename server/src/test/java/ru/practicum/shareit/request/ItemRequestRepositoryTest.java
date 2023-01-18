package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User user1;
    private ItemRequest itemRequest;

    @BeforeEach
    void init() {

        user = User.builder()
                .name("Oleg")
                .email("oleg@email.com")
                .build();

        user1 = User.builder()
                .name("Roma")
                .email("roma@email.com")
                .build();

        itemRequest = ItemRequest
                .builder()
                .description("описание запроса")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void findAllByRequestorIdOrderByCreatedAscTest() {
        userRepository.save(user);
        itemRequestRepository.save(itemRequest);

        List<ItemRequest> items = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(user.getId());

        assertThat(items.size(), equalTo(1));
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedAscTest() {
        userRepository.save(user);
        itemRequestRepository.save(itemRequest);

        assertThat((long) itemRequestRepository.findAllByRequestorIdNotOrderByCreatedAsc(user.getId(),
                Pageable.ofSize(10)).size(), equalTo(0L));

        userRepository.save(user1);

        assertThat((long) itemRequestRepository.findAllByRequestorIdNotOrderByCreatedAsc(user1.getId(),
                Pageable.ofSize(10)).size(), equalTo(1L));
    }
}
