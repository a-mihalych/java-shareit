package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user1;
    private User user2;
    private ItemRequest request;

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
        request = ItemRequest.builder()
                             .description("ItemRequestDescription")
                             .created(LocalDateTime.now())
                             .requestor(user1)
                             .build();
        userRepository.save(user1);
        userRepository.save(user2);
        requestRepository.save(request);
    }

    @AfterEach
    void afterEach() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc() {
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(user1.getId());
        assertEquals(1, requests.size());
        assertEquals(request.getId(), requests.get(0).getId());
        assertEquals(user1.getId(), requests.get(0).getRequestor().getId());
    }

    @Test
    void itemsRequestAllForUser() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ItemRequest> requestsPage = requestRepository.itemsRequestAllForUser(user2.getId(), pageRequest);
        List<ItemRequest> requests = requestsPage.getContent();
        assertEquals(1, requests.size());
        assertEquals(request.getId(), requests.get(0).getId());
        assertEquals(user1.getId(), requests.get(0).getRequestor().getId());
    }
}