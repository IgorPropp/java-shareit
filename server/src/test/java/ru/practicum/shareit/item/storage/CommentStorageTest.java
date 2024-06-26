package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentStorageTest {

    @Autowired
    private CommentStorage commentRepository;
    @Autowired
    private ItemStorage itemRepository;
    @Autowired
    private UserStorage userRepository;
    private Item savedItem1;
    private Item savedItem2;
    private Item savedItem3;
    private Comment savedComment1;
    private Comment savedComment2;
    private Comment savedComment3;

    @BeforeAll
    void setUp() {
        User user1 = createUser(1);
        User savedUser1 = userRepository.save(user1);
        User user2 = createUser(2);
        User savedUser2 = userRepository.save(user2);

        Item item1 = createItem(1);
        item1.setOwner(savedUser1);
        savedItem1 = itemRepository.save(item1);
        Item item2 = createItem(2);
        item2.setAvailable(false);
        item2.setOwner(savedUser1);
        savedItem2 = itemRepository.save(item2);
        Item item3 = createItem(3);
        item3.setOwner(savedUser2);
        savedItem3 = itemRepository.save(item3);

        Comment comment1 = createComment(1);
        comment1.setUser(savedUser2);
        comment1.setItem(savedItem1);
        savedComment1 = commentRepository.save(comment1);

        Comment comment2 = createComment(2);
        comment2.setUser(savedUser2);
        comment2.setItem(savedItem2);
        savedComment2 = commentRepository.save(comment2);

        Comment comment3 = createComment(4);
        comment3.setUser(savedUser2);
        comment3.setItem(savedItem2);
        savedComment3 = commentRepository.save(comment3);
    }

    @AfterAll
    public void clean() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testGetByItemId() {
        List<Comment> comments = commentRepository.getByItem_IdOrderByCreatedDesc(savedItem2.getId());

        assertThat(comments, notNullValue());
        assertThat(comments.size(), is(2));
        assertThat(comments.get(0).getId(), is(savedComment3.getId()));
        assertThat(comments.get(0).getText(), is(savedComment3.getText()));
        assertThat(comments.get(1).getId(), is(savedComment2.getId()));
        assertThat(comments.get(1).getText(), is(savedComment2.getText()));
    }

    @Test
    void testGetByItemIdEmptyComments() {
        List<Comment> comments = commentRepository.getByItem_IdOrderByCreatedDesc(savedItem3.getId());

        assertThat(comments, notNullValue());
        assertThat(comments.size(), is(0));
    }

    @Test
    void testGetByItemIdNotExist() {
        List<Comment> comments = commentRepository.getByItem_IdOrderByCreatedDesc(999L);

        assertThat(comments, notNullValue());
        assertThat(comments.size(), is(0));
    }

    @Test
    void testGetByItemIdIn() {
        List<Comment> comments = commentRepository.getByItem_IdIn(List.of(savedItem1.getId(), savedItem2.getId()));

        assertThat(comments, notNullValue());
        assertThat(comments.size(), is(3));
        assertThat(comments.get(0).getId(), is(savedComment1.getId()));
        assertThat(comments.get(0).getText(), is(savedComment1.getText()));
        assertThat(comments.get(1).getId(), is(savedComment2.getId()));
        assertThat(comments.get(1).getText(), is(savedComment2.getText()));
        assertThat(comments.get(2).getId(), is(savedComment3.getId()));
        assertThat(comments.get(2).getText(), is(savedComment3.getText()));
    }

    @Test
    void testGetByItemIdInSingleItem() {
        List<Comment> comments = commentRepository.getByItem_IdIn(List.of(savedItem2.getId()));

        assertThat(comments, notNullValue());
        assertThat(comments.size(), is(2));
        assertThat(comments.get(0).getId(), is(savedComment2.getId()));
        assertThat(comments.get(0).getText(), is(savedComment2.getText()));
        assertThat(comments.get(1).getId(), is(savedComment3.getId()));
        assertThat(comments.get(1).getText(), is(savedComment3.getText()));
    }

    private Item createItem(int id) {
        return Item.builder()
                .name("item name " + id)
                .description("item description " + id)
                .available(true)
                .build();
    }

    private User createUser(int id) {
        return User.builder()
                .name("user" + id)
                .email("requester" + id + "@email.com")
                .build();
    }

    private Comment createComment(int id) {
        return Comment.builder()
                .text("comment " + id)
                .created(LocalDateTime.now())
                .build();
    }
}
