package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    private Item item;
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
        item = Item.builder()
                .name("nameItem")
                .description("descriptionItem")
                .available(true)
                .owner(user2)
                .itemRequest(request)
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        requestRepository.save(request);
        itemRepository.save(item);
    }

    @AfterEach
    void afterEach() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void findAllByOwnerIdOrderByIdAsc() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Item> itemsPage = itemRepository.findAllByOwnerIdOrderByIdAsc(user2.getId(), pageRequest);
        List<Item> items = itemsPage.getContent();
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
        assertEquals(user2.getId(), items.get(0).getOwner().getId());
        assertEquals(request.getId(), items.get(0).getItemRequest().getId());
    }

    @Test
    void findItem() {
        String search = "descriptionitem";
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Item> itemsPage = itemRepository.findItem(search, pageRequest);
        List<Item> items = itemsPage.getContent();
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
        assertEquals(user2.getId(), items.get(0).getOwner().getId());
        assertEquals(request.getId(), items.get(0).getItemRequest().getId());
    }

    @Test
    void findAllByItemRequestId() {
        List<Item> items = itemRepository.findAllByItemRequestId(item.getItemRequest().getId());
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
        assertEquals(user2.getId(), items.get(0).getOwner().getId());
        assertEquals(request.getId(), items.get(0).getItemRequest().getId());
    }
}