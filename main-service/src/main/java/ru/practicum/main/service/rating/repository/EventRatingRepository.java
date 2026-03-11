package ru.practicum.main.service.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.service.rating.model.EventRating;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRatingRepository extends JpaRepository<EventRating, Long> {

    Optional<EventRating> findByEventIdAndUserId(Long eventId, Long userId);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    @Query("SELECT r.eventId, " +
            "COUNT(CASE WHEN r.ratingType = 'LIKE' THEN 1 END), " +
            "COUNT(CASE WHEN r.ratingType = 'DISLIKE' THEN 1 END) " +
            "FROM EventRating r " +
            "WHERE r.eventId = :eventId " +
            "GROUP BY r.eventId")
    List<Object[]> getRatingByEventId(@Param("eventId") Long eventId);

    @Query("SELECT r.userId, " +
            "COUNT(CASE WHEN r.ratingType = 'LIKE' THEN 1 END), " +
            "COUNT(CASE WHEN r.ratingType = 'DISLIKE' THEN 1 END) " +
            "FROM EventRating r " +
            "WHERE r.userId = :userId " +
            "GROUP BY r.userId")
    List<Object[]> getRatingByUserId(@Param("userId") Long userId);

    @Query("SELECT r.eventId, " +
            "(COUNT(CASE WHEN r.ratingType = 'LIKE' THEN 1 END) - " +
            "COUNT(CASE WHEN r.ratingType = 'DISLIKE' THEN 1 END)) as rating " +
            "FROM EventRating r " +
            "GROUP BY r.eventId " +
            "ORDER BY rating DESC " +
            "LIMIT :limit")
    List<Object[]> findTopRatedEvents(@Param("limit") int limit);

    @Modifying
    @Transactional
    @Query("DELETE FROM EventRating r WHERE r.eventId = :eventId AND r.userId = :userId")
    void deleteByEventIdAndUserId(@Param("eventId") Long eventId, @Param("userId") Long userId);
}