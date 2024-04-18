package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner(User userId);

    @Query("SELECT i FROM Item i WHERE" +
            " (LOWER(i.name) LIKE %:string% OR LOWER(i.description) LIKE %:string%)" +
            " AND i.available = true")
    List<Item> findByNameOrDescriptionContainingIgnoreCase(String string);

    @Query("SELECT i FROM Item i WHERE i.request.id = :id")
    List<Item> findAllByRequestId(Long id);
}
