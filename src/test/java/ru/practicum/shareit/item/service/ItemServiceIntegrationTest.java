package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {

    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final ItemMapper itemMapper;

    @Test
    void testCreateItem() {
        User user = new User(1L, "User", "testitemadd@email.ru");
        Item item1 = new Item(1L, "ItemCreateTest", "description", true, user, null);
        ItemDto itemDto = itemMapper.toDto(item1);
        itemDto.setId(0L);

        em.createNativeQuery("insert into users (name, email) values (?, ?)")
                .setParameter(1, user.getName())
                .setParameter(2, user.getEmail())
                .executeUpdate();

        TypedQuery<User> query1 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User owner = query1
                .setParameter("email", user.getEmail())
                .getSingleResult();

        itemService.createItem(owner.getId(), itemDto);

        TypedQuery<Item> query2 = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemOut = query2
                .setParameter("name", itemDto.getName())
                .getSingleResult();

        assertThat(itemOut.getId(), notNullValue());
        assertThat(itemOut.getName(), equalTo(itemDto.getName()));
        assertThat(itemOut.getDescription(), equalTo(itemDto.getDescription()));
    }
}
