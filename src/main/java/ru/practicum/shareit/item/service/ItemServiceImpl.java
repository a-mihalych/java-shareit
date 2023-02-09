package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingNextDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> itemsForId(Integer userId) {
        List<Booking> bookings = bookingRepository.findAllForItemsOwner(userId);
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);
        List<ItemDto> itemsDto = new ArrayList<>();
        BookingNextDto bookingNext;
        BookingNextDto bookingLast;
        List<Comment> comments;
        List<CommentDto> commentsDto;
        for (Item item : items) {
            bookingNext = null;
            bookingLast = null;
            for (Booking booking : bookings) {
                if (bookingNext != null && booking.getItem().getId().equals(item.getId())) {
                    bookingLast = BookingMapper.toBookingNextDto(booking);
                }
                if (bookingNext == null && booking.getItem().getId().equals(item.getId())) {
                    bookingNext = BookingMapper.toBookingNextDto(booking);
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
        List<Booking> bookings = bookingRepository.findAllByItemIdAndItemOwnerIdOrderByStartDesc(itemId, userId);
        BookingNextDto bookingNext = bookings.size() > 0 ? BookingMapper.toBookingNextDto(bookings.get(0)) : null;
        BookingNextDto bookingLast = bookings.size() > 1 ?
                BookingMapper.toBookingNextDto(bookings.get(bookings.size() - 1)) : null;
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
    public ItemDto addItem(Integer userId, ItemNewDto itemNewDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", userId));
        });
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemNewDto, user)));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Integer userId, ItemDto itemDto, Integer itemId) {
        if (itemDto.getId() != null) {
            if (!itemDto.getId().equals(itemId)) {
                throw new ForbiddenOperationException("Обновление прервано, вещь другого пользователя");
            }
        }
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", userId));
        });
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
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d", userId));
        });
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Не найдена вещь с id = %d", itemId));
        });
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndItemIdAndStartBefore(userId, itemId,
                                                                                            LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new ValidationException(String.format("Добавление коментария прервано, " +
                    "пользователь с id = %d не брал в аренду вещь с id = %d", itemId, itemId));
        }
        return ItemMapper.toCommentDto(commentRepository.save(ItemMapper.toComment(commentNewDto, user, item)));
    }
}
