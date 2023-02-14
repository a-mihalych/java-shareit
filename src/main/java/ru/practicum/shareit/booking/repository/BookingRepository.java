package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer bookerId,
                                                                             LocalDateTime nowStart,
                                                                             LocalDateTime nowEnd);

    @Query("SELECT booking from Booking booking " +
            "where booking.item.owner.id = ?1 " +
            "and booking.start < ?2 " +
            "and booking.end > ?2 " +
            "order by booking.start desc")
    List<Booking> findAllCurrentForItemsOwner(Integer userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime now);

    @Query("SELECT booking from Booking booking " +
            "where booking.item.owner.id = ?1 " +
            "and booking.end < ?2 " +
            "order by booking.start desc")
    List<Booking> findAllPastForItemsOwner(Integer userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime now);

    @Query("SELECT booking from Booking booking " +
            "where booking.item.owner.id = ?1 " +
            "and booking.start > ?2 " +
            "order by booking.start desc")
    List<Booking> findAllFutureForItemsOwner(Integer userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Integer bookerId, StatusBooking status);

    @Query("SELECT booking from Booking booking " +
            "where booking.item.owner.id = ?1 " +
            "and booking.status = ?2" +
            " order by booking.start desc")
    List<Booking> findAllForItemsOwnerByStatus(Integer userId, StatusBooking status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Integer bookerId);

    @Query("SELECT booking from Booking booking " +
           "where booking.item.owner.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findAllForItemsOwner(Integer userId);

    List<Booking> findAllByItemIdAndItemOwnerIdOrderByStartDesc(Integer itemId, Integer userId);

    List<Booking> findAllByBookerIdAndItemIdAndStartBefore(Integer userId, Integer itemId, LocalDateTime now);
}
