package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestNewDto itemRequestNewDto, Integer userId) {
        User user = UserMapper.toUser(userService.userById(userId), new User());
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestNewDto, LocalDateTime.now(), user);
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> itemsRequest(Integer userId) {
        userService.userById(userId);
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        List<ItemRequestDto> requestsDto = requests.stream()
                                                   .map(ItemRequestMapper::toItemRequestDto)
                                                   .collect(Collectors.toList());
        for (ItemRequestDto itemRequestDto : requestsDto) {
            itemRequestDto.setItems(itemService.itemByRequestId(itemRequestDto.getId()));
        }
        return requestsDto;
    }

    @Override
    public ItemRequestDto itemsRequestById(Integer userId, Integer itemRequestId) {
        userService.userById(userId);
        ItemRequest itemRequest = requestRepository.findById(itemRequestId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Не найден запрос с id = %d", itemRequestId));
        });
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemService.itemByRequestId(itemRequestDto.getId()));
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> itemsRequestAll(Integer userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        Page<ItemRequest> requestsPage = requestRepository.itemsRequestAllForUser(userId, pageRequest);
        List<ItemRequestDto> requestsDto = requestsPage.stream()
                                                       .map(ItemRequestMapper::toItemRequestDto)
                                                       .collect(Collectors.toList());
        for (ItemRequestDto itemRequestDto : requestsDto) {
            itemRequestDto.setItems(itemService.itemByRequestId(itemRequestDto.getId()));
        }
        return requestsDto;
    }
}
