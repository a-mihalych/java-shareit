package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    Page<Item> findAllByOwnerIdOrderByIdAsc(Integer userId, PageRequest pageRequest);

    @Query("SELECT item from Item item " +
           "where item.available = true " +
           "and (lower(item.name) like %?1% " +
           "or lower(item.description) like %?1%)")
    Page<Item> findItem(String text, PageRequest pageRequest);

    List<Item> findAllByItemRequestId(Integer requestId);
}
