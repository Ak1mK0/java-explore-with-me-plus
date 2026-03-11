package ru.practicum.main.service.rating.service;

import ru.practicum.main.service.rating.dto.EventRatingDto;
import ru.practicum.main.service.rating.dto.UserRatingDto;

import java.util.List;

public interface RatingService {

    EventRatingDto addLike(Long eventId, Long userId);

    EventRatingDto addDislike(Long eventId, Long userId);

    void removeLike(Long eventId, Long userId);

    void removeDislike(Long eventId, Long userId);

    EventRatingDto getEventRating(Long eventId);

    UserRatingDto getUserRating(Long userId);

    List<EventRatingDto> getTopRatedEvents(int limit);
}