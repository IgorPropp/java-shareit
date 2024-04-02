package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRequestStorageTest {

    @Autowired
    private ItemRequestStorage itemRequestStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;
    private User savedUser;
    private ItemRequest savedRequest;
    private ItemRequest savedRequest2;

    @BeforeAll
    public void setUp() {
        User user = User.builder().name("username2").email("test2@email.com").build();
        savedUser = userStorage.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(savedUser).description("description2").build();
        savedRequest = itemRequestStorage.save(itemRequest);
        ItemRequest itemRequest2 = ItemRequest.builder().requester(savedUser).description("description3").build();
        savedRequest2 = itemRequestStorage.save(itemRequest2);
    }

    @AfterAll
    public void clean() {
        itemStorage.deleteAll();
        itemRequestStorage.deleteAll();
        userStorage.deleteAll();
    }

    @Test
    public void testGetItemRequestByRequesterId() {
        List<ItemRequest> requests = itemRequestStorage.findAllByRequesterIdOrderByCreatedDesc(savedUser.getId());

        assertThat(requests.size(), is(2));
        assertThat(requests.get(0).getId(), is(savedRequest.getId()));
        assertThat(requests.get(1).getId(), is(savedRequest2.getId()));
    }
}
