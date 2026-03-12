package ru.practicum.main.service.rating.service;

import ru.practicum.main.service.rating.dto.EventRatingResponseDto;
import ru.practicum.main.service.rating.dto.EventRatingStatsDto;
import ru.practicum.main.service.rating.dto.EventRatingListDto;
import ru.practicum.main.service.rating.dto.EventRatingTopDto;

import java.util.List;

public interface RatingService {

    EventRatingResponseDto addLike(Long userId, Long eventId);

    EventRatingResponseDto addDislike(Long userId, Long eventId);

    void deleteRating(Long userId, Long eventId);

    EventRatingStatsDto getEventRatingStats(Long eventId);

    EventRatingListDto getUserRatings(Long userId, String rating, int from, int size);

    List<EventRatingTopDto> getTopRatedEvents(int from, int size, String order);
}