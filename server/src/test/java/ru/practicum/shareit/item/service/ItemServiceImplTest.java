package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exception.ForbiddenOperationException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemNewDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private ItemServiceImpl itemService;
    private Item item;
    private ItemRequest request;
    private UserDto user1Dto;
    private User user2;

    @BeforeEach
    void beforeEach() {
        user1Dto = UserDto.builder()
                         .id(1)
                         .email("user@mail.com")
                         .name("UserName")
                         .build();
        user2 = User.builder()
                    .id(2)
                    .name("User2Name")
                    .email("user2@mail.com")
                    .build();
        request = ItemRequest.builder()
                             .id(1)
                             .description("descriptionItemRequest")
                             .requestor(new User())
                             .created(LocalDateTime.now())
                             .build();
        item = Item.builder()
                   .id(1)
                   .name("nameItem")
                   .description("descriptionItem")
                   .available(true)
                   .owner(user2)
                   .itemRequest(request)
                   .build();
    }

    @Test
    void itemsForId_listItemDto() {
        BookingDto bookingDto1 = BookingDto.builder()
                                           .id(1)
                                           .item(item)
                                           .booker(user2)
                                           .build();
        BookingDto bookingDto2 = BookingDto.builder()
                                           .id(2)
                                           .item(item)
                                           .booker(user2)
                                           .build();
        Integer from = 0;
        Integer size = 10;
        PageImpl<Item> page = new PageImpl<>(List.of(item));
        when(bookingService.bookingAllItemForUserId(anyInt(), any(StatusBooking.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto1, bookingDto2));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyInt(), any(PageRequest.class))).thenReturn(page);
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(anyInt())).thenReturn(List.of());
        List<ItemDto> itemsDto = itemService.itemsForId(user2.getId(), from, size);
        assertEquals(1, itemsDto.size());
        assertEquals(item.getId(), itemsDto.get(0).getId());
        assertEquals(item.getName(), itemsDto.get(0).getName());
        assertEquals(item.getDescription(), itemsDto.get(0).getDescription());
        assertTrue(itemsDto.get(0).getAvailable());
        assertEquals(bookingDto1.getId(), itemsDto.get(0).getNextBooking().getId());
        assertEquals(bookingDto2.getId(), itemsDto.get(0).getLastBooking().getId());
    }

    @Test
    void itemById_itemDto() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userService.userById(anyInt())).thenReturn(user1Dto);
        when(bookingService.bookingItemForOwnerId(anyInt(), anyInt())).thenReturn(List.of());
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(anyInt())).thenReturn(List.of());
        ItemDto itemDto = itemService.itemById(user1Dto.getId(), item.getId());
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
    }

    @Test
    void itemById_notFoundException() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.itemById(user2.getId(), item.getId()));
    }

    @Test
    void searchItem_listItemDto() {
        Integer from = 0;
        Integer size = 10;
        PageImpl<Item> page = new PageImpl<>(List.of(item));
        String text = "nameITEM";
        when(itemRepository.findItem(anyString(), any(PageRequest.class))).thenReturn(page);
        List<ItemDto> itemsDto = itemService.searchItem(user2.getId(), text, from, size);
        assertEquals(1, itemsDto.size());
        assertEquals(item.getId(), itemsDto.get(0).getId());
        assertEquals(item.getName(), itemsDto.get(0).getName());
        assertEquals(item.getDescription(), itemsDto.get(0).getDescription());
        assertTrue(itemsDto.get(0).getAvailable());
    }

    @Test
    void searchItem_listItemDtoEmpty() {
        Integer from = 0;
        Integer size = 10;
        String text = "";
        List<ItemDto> itemsDto = itemService.searchItem(user2.getId(), text, from, size);
        assertEquals(0, itemsDto.size());
    }

    @Test
    void addItem_itemDto() {
        ItemNewDto itemNewDto = ItemNewDto.builder()
                .name("nameItemNewDto")
                .available(true)
                .description("descriptionItemNewDto")
                .build();
        when(userService.userById(anyInt())).thenReturn(user1Dto);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto itemDto = itemService.addItem(user2.getId(), itemNewDto, ItemRequestMapper.toItemRequestDto(request));
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(request.getId(), itemDto.getRequestId());
        assertTrue(itemDto.getAvailable());
    }

    @Test
    void updateItem_itemDto() {
        ItemDto itemDto = ItemDto.builder()
                                 .name("nameItemDto")
                                 .description("descriptionItemDto")
                                 .available(true)
                                 .build();
        when(userService.userById(anyInt())).thenReturn(user1Dto);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        itemDto = itemService.updateItem(user2.getId(), itemDto, item.getId());
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
    }

    @Test
    void updateItem_forbiddenOperationException1() {
        ItemDto itemDto = ItemDto.builder()
                                 .id(item.getId() + 1)
                                 .name("nameItemDto")
                                 .description("descriptionItemDto")
                                 .available(true)
                                 .build();
        assertThrows(ForbiddenOperationException.class,
                () -> itemService.updateItem(user2.getId(), itemDto, item.getId()));
    }

    @Test
    void updateItem_notFoundException() {
        ItemDto itemDto = ItemDto.builder()
                .name("nameItemDto")
                .description("descriptionItemDto")
                .available(true)
                .build();
        when(userService.userById(anyInt())).thenReturn(user1Dto);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.updateItem(user2.getId(), itemDto, item.getId()));
    }

    @Test
    void updateItem_forbiddenOperationException2() {
        ItemDto itemDto = ItemDto.builder()
                .name("nameItemDto")
                .description("descriptionItemDto")
                .available(true)
                .build();
        when(userService.userById(anyInt())).thenReturn(user1Dto);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        assertThrows(ForbiddenOperationException.class,
                () -> itemService.updateItem(user2.getId() + 1, itemDto, item.getId()));
    }

    @Test
    void addComment_commentDto() {
        CommentNewDto commentNewDto = CommentNewDto.builder()
                                                   .text("textComment")
                                                   .build();
        Comment comment = Comment.builder()
                                 .id(1)
                                 .text("textComment")
                                 .author(user2)
                                 .created(LocalDateTime.now())
                                 .build();
        BookingDto bookingDto = BookingDto.builder()
                                          .id(1)
                                          .item(item)
                                          .start(LocalDateTime.now())
                                          .end(LocalDateTime.now())
                                          .booker(user2)
                                          .status(StatusBooking.CANCELED)
                                          .build();
        when(userService.userById(anyInt())).thenReturn(user1Dto);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingService.bookingItemForBookerId(anyInt(), anyInt(), any(LocalDateTime.class)))
                .thenReturn(List.of(bookingDto));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto commentDto = itemService.addComment(user1Dto.getId(), item.getId(), commentNewDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
    }

    @Test
    void addComment_notFoundException() {
        CommentNewDto commentNewDto = CommentNewDto.builder()
                .text("textComment")
                .build();
        when(userService.userById(anyInt())).thenReturn(user1Dto);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemService.addComment(user1Dto.getId(), item.getId(), commentNewDto));
    }

    @Test
    void addComment_validationException() {
        CommentNewDto commentNewDto = CommentNewDto.builder()
                .text("textComment")
                .build();
        when(userService.userById(anyInt())).thenReturn(user1Dto);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingService.bookingItemForBookerId(anyInt(), anyInt(), any(LocalDateTime.class)))
                .thenReturn(List.of());
        assertThrows(ValidationException.class,
                () -> itemService.addComment(user1Dto.getId(), item.getId() + 1, commentNewDto));
    }

    @Test
    void itemByRequestId_listItemDto() {
        when(itemRepository.findAllByItemRequestId(anyInt())).thenReturn(List.of(item));
        List<ItemDto> itemsDto = itemService.itemByRequestId(request.getId());
        assertEquals(1, itemsDto.size());
        assertEquals(item.getId(), itemsDto.get(0).getId());
        assertEquals(item.getDescription(), itemsDto.get(0).getDescription());
        assertTrue(itemsDto.get(0).getAvailable());
        assertEquals(item.getItemRequest().getId(), itemsDto.get(0).getRequestId());
    }
}
