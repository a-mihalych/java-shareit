package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingNextDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemNewDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                      .id(item.getId())
                      .name(item.getName())
                      .description(item.getDescription())
                      .available(item.getAvailable())
                      .owner(item.getOwner())
                      .requestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null)
                      .build();
    }

    public static ItemDto toItemDto(Item item, BookingNextDto bookingNext,
                                    BookingNextDto bookingLast, List<CommentDto> commentsDto) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .nextBooking(bookingNext)
                .lastBooking(bookingLast)
                .comments(commentsDto)
                .build();
    }

    public static Item toItem(ItemDto itemDto, Item item) {
        return Item.builder()
                   .id(itemDto.getId() != null ? itemDto.getId() : item.getId())
                   .name(itemDto.getName() != null ? itemDto.getName() : item.getName())
                   .description(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription())
                   .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable())
                   .owner(itemDto.getOwner() != null ? itemDto.getOwner() : item.getOwner())
                   .build();
    }

    public static Item toItem(ItemNewDto itemNewDto, User user, ItemRequest itemRequest) {
        return Item.builder()
                .name(itemNewDto.getName())
                .description(itemNewDto.getDescription())
                .available(itemNewDto.getAvailable())
                .owner(user)
                .itemRequest(itemRequest)
                .build();
    }

    public static Comment toComment(CommentNewDto commentNewDto, User user, Item item) {
        return Comment.builder()
                      .text(commentNewDto.getText())
                      .author(user)
                      .item(item)
                      .created(LocalDateTime.now())
                      .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                         .id(comment.getId())
                         .text(comment.getText())
                         .authorName(comment.getAuthor().getName())
                         .created(comment.getCreated())
                         .build();
    }
}
