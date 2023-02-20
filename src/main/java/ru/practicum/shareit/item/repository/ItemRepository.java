package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByOwnerIdOrderByIdAsc(Integer userId);

    @Query("SELECT item from Item item " +
           "where item.available = true " +
           "and (lower(item.name) like %?1% " +
           "or lower(item.description) like %?1%)")
    List<Item> findItem(String text);

    List<Item> findAllByItemRequestId(Integer requestId);
}
