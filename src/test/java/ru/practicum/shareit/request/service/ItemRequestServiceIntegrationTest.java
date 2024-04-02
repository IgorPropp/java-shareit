package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
class ItemRequestServiceIntegrationTest {

    private final EntityManager em;
    private final ItemRequestServiceImpl requestService;

    @Test
    void testCreateItemRequest() {
        User user = new User(1L, "Requester", "Requester@email.ru");
        em.createNativeQuery("insert into users (name, email) values (?, ?)")
                .setParameter(1, user.getName())
                .setParameter(2, user.getEmail())
                .executeUpdate();

        TypedQuery<User> query1 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User requester = query1
                .setParameter("email", user.getEmail())
                .getSingleResult();

        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "request description", requester, null);
        requestService.createItemRequest(requester.getId(), itemRequestDto);

        TypedQuery<ItemRequest> query2 =
                em.createQuery("Select r from ItemRequest r where r.requester.id = :requester_id", ItemRequest.class);
        ItemRequest request = query2.setParameter("requester_id", requester.getId()).getSingleResult();

        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(request.getRequester(), equalTo(itemRequestDto.getRequester()));
        assertThat(request.getCreated(), notNullValue());
    }
}
