package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private ItemRepository itemRepository;
    private Comment comment;
    private User user1;
    private User user2;
    private User user3;
    private ItemRequest request;
    private Item item;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                .email("user1@mail.com")
                .name("User1Name")
                .build();
        user2 = User.builder()
                .email("user2@mail.com")
                .name("User2Name")
                .build();
        user3 = User.builder()
                .email("user3@mail.com")
                .name("User3Name")
                .build();
        request = ItemRequest.builder()
                .description("ItemRequestDescription")
                .created(LocalDateTime.now())
                .requestor(user1)
                .build();
        item = Item.builder()
                .name("nameItem")
                .description("descriptionItem")
                .available(true)
                .owner(user2)
                .itemRequest(request)
                .build();
        comment = Comment.builder()
                .text("textComment")
                .item(item)
                .author(user3)
                .created(LocalDateTime.now())
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        requestRepository.save(request);
        itemRepository.save(item);
        commentRepository.save(comment);
    }

    @AfterEach
    void afterEach() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void findAllByItemIdOrderByCreatedDesc() {
        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());
        assertEquals(1, comments.size());
        assertEquals(comment.getId(), comments.get(0).getId());
        assertEquals(user3.getId(), comments.get(0).getAuthor().getId());
        assertEquals(item.getId(), comments.get(0).getItem().getId());
    }
}