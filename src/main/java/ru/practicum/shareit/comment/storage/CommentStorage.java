package ru.practicum.shareit.comment.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Long> {
    List<Comment> getByItem_IdOrderByCreatedDesc(Long itemId);
    List<Comment> getByItem_IdIn(List<Long> itemId);
}