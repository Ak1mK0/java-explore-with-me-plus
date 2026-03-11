package ru.practicum.main.service.rating.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.main.service.rating.dto.EventRatingDto;
import ru.practicum.main.service.rating.dto.UserRatingDto;
import ru.practicum.main.service.rating.model.EventRating;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RatingMapper {

    public static EventRating toEntity(Integer eventId, Integer userId, EventRating.RatingType type) {
        return EventRating.builder()
                .eventId(eventId.longValue())
                .userId(userId.longValue())
                .ratingType(type)
                .created(LocalDateTime.now())
                .build();
    }

    public static EventRatingDto toEventRatingDto(Integer eventId, Integer likes, Integer dislikes) {
        return EventRatingDto.builder()
                .eventId(eventId)
                .likes(likes)
                .dislikes(dislikes)
                .rating(likes - dislikes)
                .build();
    }

    public static UserRatingDto toUserRatingDto(Integer userId, Integer totalLikes, Integer totalDislikes) {
        return UserRatingDto.builder()
                .userId(userId)
                .totalLikes(totalLikes)
                .totalDislikes(totalDislikes)
                .totalRating(totalLikes - totalDislikes)
                .build();
    }
}