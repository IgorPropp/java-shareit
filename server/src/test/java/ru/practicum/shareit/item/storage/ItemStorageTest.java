package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemStorageTest {

    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;
    private Item savedItem1;
    private Item savedItem2;
    private Item savedItem3;
    private User savedUser1;

    @BeforeAll
    void setUp() {
        User user1 = createUser(1);
        savedUser1 = userStorage.save(user1);
        User user2 = createUser(2);
        User savedUser2 = userStorage.save(user2);

        Item item1 = createItem(1);
        item1.setOwner(savedUser1);
        savedItem1 = itemStorage.save(item1);
        Item item2 = createItem(2);
        item2.setAvailable(false);
        item2.setOwner(savedUser1);
        savedItem2 = itemStorage.save(item2);
        Item item3 = createItem(3);
        item3.setOwner(savedUser2);
        savedItem3 = itemStorage.save(item3);
    }

    @AfterAll
    public void clean() {
        itemStorage.deleteAll();
        userStorage.deleteAll();
    }

    @Test
    void testFindByOwner() {
        List<Item> items = itemStorage.findAllByOwner(savedUser1);

        assertThat(items, notNullValue());
        assertThat(items.size(), is(2));
        assertThat(items.get(0).getId(), is(savedItem1.getId()));
        assertThat(items.get(1).getId(), is(savedItem2.getId()));
    }

    @Test
    void testSearch() {
        String text = "%name%";
        List<Item> items = itemStorage.findByNameOrDescriptionContainingIgnoreCase(text);

        assertThat(items, notNullValue());
        assertThat(items.size(), is(2));
        assertThat(items.get(0).getId(), is(savedItem1.getId()));
        assertThat(items.get(1).getId(), is(savedItem3.getId()));
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
}
