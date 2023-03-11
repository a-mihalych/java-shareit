package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Integer userId);

    @Query("SELECT request from ItemRequest request " +
           "where request.requestor.id != ?1 " +
           "order by request.created desc")
    Page<ItemRequest> itemsRequestAllForUser(Integer userId, Pageable pageable);
}
