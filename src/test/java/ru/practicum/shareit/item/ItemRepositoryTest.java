package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User user;
    private User user1;
    private Item item;
    private Comment comment;

    @BeforeEach
    void init() {

        user = User.builder()
                .name("Oleg")
                .email("oleg@email.com")
                .build();

        user1 = User.builder()
                .name("Ivan")
                .email("ivan@email.com")
                .build();

        item = Item.builder()
                .name("Молоток")
                .description("молоток забивной")
                .available(true)
                .owner(user)
                .build();

        comment = Comment.builder()
                .text("комментарий")
                .item(item)
                .author(user1)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void getItemByTextTest() {
        userRepository.save(user);
        itemRepository.save(item);

        Page<Item> items = itemRepository.getItemByText("молоток", Pageable.ofSize(10));

        assertThat(items.stream().count(), equalTo(1L));
    }

    @Test
    void findAllByOwnerIdTest() {
        userRepository.save(user);
        itemRepository.save(item);

        Page<Item> items = itemRepository.findAllByOwnerId(user.getId(), Pageable.ofSize(10));

        assertThat(items.stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemIdTest() {
        userRepository.save(user);
        userRepository.save(user1);
        itemRepository.save(item);
        commentRepository.save(comment);

        assertThat(commentRepository.findAllByItemId(item.getId()).size(), equalTo(1));
    }
}
