package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

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
class UserServiceIntegrationTest {

    private final EntityManager em;
    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    @Test
    void add() {
        User user = new User(0L, "Username", "some@email.com");
        User user1 = userService.createUser(user);

        assertThat(user1.getId(), notNullValue());
        assertThat(user1.getName(), equalTo(user.getName()));
        assertThat(user1.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void updateUser() {
        User user = new User(1L, "user Name", "user@email.ru");
        em.createNativeQuery("insert into users (NAME, email) values (?, ?)")
                .setParameter(1, user.getName())
                .setParameter(2, user.getEmail())
                .executeUpdate();
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user1 = query
                .setParameter("email", user.getEmail())
                .getSingleResult();

        User updateUser = new User(user1.getId(), "user updateName", "user@email.ru");
        assertThat(userService.updateUser(updateUser.getId(), userMapper.toDto(updateUser)), equalTo(userMapper.toDto(updateUser)));
    }
}
