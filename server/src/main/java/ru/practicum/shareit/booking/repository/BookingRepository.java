package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Integer bookerId, LocalDateTime nowStart, LocalDateTime nowEnd, Pageable pageable);

    @Query("SELECT booking from Booking booking " +
            "where booking.item.owner.id = ?1 " +
            "and booking.start < ?2 " +
            "and booking.end > ?2 " +
            "order by booking.start desc")
    Page<Booking> findAllCurrentForItemsOwner(Integer userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime now, Pageable pageable);

    @Query("SELECT booking from Booking booking " +
            "where booking.item.owner.id = ?1 " +
            "and booking.end < ?2 " +
            "order by booking.start desc")
    Page<Booking> findAllPastForItemsOwner(Integer userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime now, Pageable pageable);

    @Query("SELECT booking from Booking booking " +
            "where booking.item.owner.id = ?1 " +
            "and booking.start > ?2 " +
            "order by booking.start desc")
    Page<Booking> findAllFutureForItemsOwner(Integer userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Integer bookerId, StatusBooking status, Pageable pageable);

    @Query("SELECT booking from Booking booking " +
            "where booking.item.owner.id = ?1 " +
            "and booking.status = ?2" +
            " order by booking.start desc")
    Page<Booking> findAllForItemsOwnerByStatus(Integer userId, StatusBooking status, Pageable pageable);

    Page<Booking> findAllByBookerIdOrderByStartDesc(Integer bookerId, Pageable pageable);

    @Query("SELECT booking from Booking booking " +
           "where booking.item.owner.id = ?1 " +
            "order by booking.start desc")
    Page<Booking> findAllForItemsOwner(Integer userId, Pageable pageable);

    List<Booking> findAllByItemIdAndItemOwnerIdOrderByStartDesc(Integer itemId, Integer userId);

    List<Booking> findAllByBookerIdAndItemIdAndStartBefore(Integer userId, Integer itemId, LocalDateTime now);
}
