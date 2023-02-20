package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNextDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exception.ForbiddenOperationException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemNewDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;

    @Override
    public List<ItemDto> itemsForId(Integer userId) {
        List<BookingDto> bookingsDto = bookingService.bookingAllItemForUserId(userId, StatusBooking.ALL, 0, 10);
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);
        List<ItemDto> itemsDto = new ArrayList<>();
        BookingNextDto bookingNext;
        BookingNextDto bookingLast;
        List<Comment> comments;
        List<CommentDto> commentsDto;
        for (Item item : items) {
            bookingNext = null;
            bookingLast = null;
            for (BookingDto bookingDto : bookingsDto) {
                if (bookingNext != null && bookingDto.getItem().getId().equals(item.getId())) {
                    bookingLast = BookingMapper.toBookingNextDto(bookingDto);
                }
                if (bookingNext == null && bookingDto.getItem().getId().equals(item.getId())) {
                    bookingNext = BookingMapper.toBookingNextDto(bookingDto);
                }
            }
            comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());
            commentsDto = comments.stream()
                                  .map(ItemMapper::toCommentDto)
                                  .collect(Collectors.toList());
            itemsDto.add(ItemMapper.toItemDto(item, bookingNext, bookingLast, commentsDto));
        }
        return itemsDto;
    }

    @Override
    public ItemDto itemById(Integer userId, Integer itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Не найдена вещь с id = %d", itemId));
        });
        userService.userById(userId);
        List<BookingDto> bookingsDto = bookingService.bookingItemForOwnerId(userId, itemId);
        BookingNextDto bookingNext = bookingsDto.size() > 0 ? BookingMapper.toBookingNextDto(bookingsDto.get(0)) : null;
        BookingNextDto bookingLast = bookingsDto.size() > 1 ?
                BookingMapper.toBookingNextDto(bookingsDto.get(bookingsDto.size() - 1)) : null;
        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(itemId);
        List<CommentDto> commentsDto = comments.stream()
                                               .map(ItemMapper::toCommentDto)
                                               .collect(Collectors.toList());
        return ItemMapper.toItemDto(item, bookingNext, bookingLast, commentsDto);
    }

    @Override
    public List<ItemDto> searchItem(Integer userId, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findItem(text.toLowerCase());
        return items.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto addItem(Integer userId, ItemNewDto itemNewDto, ItemRequestDto requestDto) {
        User user = UserMapper.toUser(userService.userById(userId), new User());
        ItemRequest itemRequest = null;
        if (requestDto != null) {
            itemRequest = ItemRequestMapper.toItemRequest(requestDto, user);
        }
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemNewDto, user, itemRequest)));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Integer userId, ItemDto itemDto, Integer itemId) {
        if (itemDto.getId() != null) {
            if (!itemDto.getId().equals(itemId)) {
                throw new ForbiddenOperationException("Обновление прервано, вещь другого пользователя");
            }
        }
        userService.userById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Не найдена вещь с id = %d", itemId));
        });
        if (!userId.equals(item.getOwner().getId())) {
            throw new ForbiddenOperationException(String.format(
                    "Обновление прервано, у вас нет прав на обновление вещи с id = %d", userId));
        }
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, item)));
    }

    @Override
    @Transactional
    public CommentDto addComment(Integer userId, Integer itemId, CommentNewDto commentNewDto) {
        User user = UserMapper.toUser(userService.userById(userId), new User());
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Не найдена вещь с id = %d", itemId));
        });
        List<BookingDto> bookingsDto = bookingService.bookingItemForBookerId(userId, itemId, LocalDateTime.now());
        if (bookingsDto.isEmpty()) {
            throw new ValidationException(String.format("Добавление коментария прервано, " +
                    "пользователь с id = %d не брал в аренду вещь с id = %d", itemId, itemId));
        }
        return ItemMapper.toCommentDto(commentRepository.save(ItemMapper.toComment(commentNewDto, user, item)));
    }

    @Override
    public List<ItemDto> itemByRequestId(Integer requestId) {
        return itemRepository.findAllByItemRequestId(requestId).stream()
                                                               .map(ItemMapper::toItemDto)
                                                               .collect(Collectors.toList());
    }
}
