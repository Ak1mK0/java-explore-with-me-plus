package ru.practicum.main.service.rating.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.service.event.model.Event;
import ru.practicum.main.service.event.model.EventState;
import ru.practicum.main.service.event.repository.EventRepository;
import ru.practicum.main.service.exception.EventNotFoundException;
import ru.practicum.main.service.exception.OperationConditionsNotMetException;
import ru.practicum.main.service.exception.UserNotFoundException;
import ru.practicum.main.service.rating.dto.EventRatingDto;
import ru.practicum.main.service.rating.dto.UserRatingDto;
import ru.practicum.main.service.rating.mapper.RatingMapper;
import ru.practicum.main.service.rating.model.EventRating;
import ru.practicum.main.service.rating.repository.EventRatingRepository;
import ru.practicum.main.service.rating.service.RatingService;
import ru.practicum.main.service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final EventRatingRepository ratingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EventRatingDto addLike(Long eventId, Long userId) {
        return addRating(eventId, userId, EventRating.RatingType.LIKE);
    }

    @Override
    @Transactional
    public EventRatingDto addDislike(Long eventId, Long userId) {
        return addRating(eventId, userId, EventRating.RatingType.DISLIKE);
    }

    private EventRatingDto addRating(Long eventId, Long userId, EventRating.RatingType type) {
        log.info("Добавление оценки {} для события id={} пользователем id={}", type, eventId, userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Событие с id=" + eventId + " не найдено"));

        userRepository.findById(userId.intValue())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + userId + " не найден"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new OperationConditionsNotMetException("Нельзя оценивать неопубликованное событие");
        }

        if (event.getInitiator().getId().equals(userId.intValue())) {
            throw new OperationConditionsNotMetException("Нельзя оценивать собственное событие");
        }

        ratingRepository.findByEventIdAndUserId(eventId, userId)
                .ifPresentOrElse(
                        existingRating -> {
                            if (existingRating.getRatingType() == type) {
                                log.debug("Оценка уже существует: {}", existingRating);
                                return;
                            }
                            existingRating.setRatingType(type);
                            existingRating.setCreated(LocalDateTime.now());
                            ratingRepository.save(existingRating);
                            log.debug("Оценка обновлена: {}", existingRating);
                        },
                        () -> {
                            EventRating newRating = RatingMapper.toEntity(
                                    eventId.intValue(),
                                    userId.intValue(),
                                    type
                            );
                            ratingRepository.save(newRating);
                            log.debug("Создана новая оценка: {}", newRating);
                        }
                );

        return getEventRating(eventId);
    }

    @Override
    @Transactional
    public void removeLike(Long eventId, Long userId) {
        removeRating(eventId, userId, EventRating.RatingType.LIKE);
    }

    @Override
    @Transactional
    public void removeDislike(Long eventId, Long userId) {
        removeRating(eventId, userId, EventRating.RatingType.DISLIKE);
    }

    private void removeRating(Long eventId, Long userId, EventRating.RatingType type) {
        log.info("Удаление оценки {} для события id={} пользователем id={}", type, eventId, userId);

        EventRating rating = ratingRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new OperationConditionsNotMetException(
                        "Оценка не найдена для события id=" + eventId + " и пользователя id=" + userId));

        if (rating.getRatingType() != type) {
            throw new OperationConditionsNotMetException(
                    "Невозможно удалить " + type + ": у пользователя не установлен " + type);
        }

        ratingRepository.delete(rating);
        log.debug("Оценка удалена");
    }

    @Override
    public EventRatingDto getEventRating(Long eventId) {
        log.info("Получение рейтинга события id={}", eventId);

        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Событие с id=" + eventId + " не найдено");
        }

        List<Object[]> ratingData = ratingRepository.getRatingByEventId(eventId);

        if (ratingData.isEmpty()) {
            return RatingMapper.toEventRatingDto(eventId.intValue(), 0, 0);
        }

        Object[] data = ratingData.get(0);

        Integer eventIdFromDb = ((Number) data[0]).intValue();
        Integer likes = ((Number) data[1]).intValue();
        Integer dislikes = ((Number) data[2]).intValue();

        return RatingMapper.toEventRatingDto(eventIdFromDb, likes, dislikes);
    }

    @Override
    public UserRatingDto getUserRating(Long userId) {
        log.info("Получение рейтинга пользователя id={}", userId);

        if (!userRepository.existsById(userId.intValue())) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        }

        List<Object[]> ratingData = ratingRepository.getRatingByUserId(userId);

        if (ratingData.isEmpty()) {
            return RatingMapper.toUserRatingDto(userId.intValue(), 0, 0);
        }

        Object[] data = ratingData.get(0);

        Integer userIdFromDb = ((Number) data[0]).intValue();
        Integer totalLikes = ((Number) data[1]).intValue();
        Integer totalDislikes = ((Number) data[2]).intValue();

        return RatingMapper.toUserRatingDto(userIdFromDb, totalLikes, totalDislikes);
    }

    @Override
    public List<EventRatingDto> getTopRatedEvents(int limit) {
        log.info("Получение топ-{} событий по рейтингу", limit);

        List<Object[]> topEvents = ratingRepository.findTopRatedEvents(limit);
        List<EventRatingDto> result = new ArrayList<>();

        for (Object[] data : topEvents) {
            Integer eventId = ((Number) data[0]).intValue();
            Integer rating = ((Number) data[1]).intValue();

            List<Object[]> eventRatingData = ratingRepository.getRatingByEventId(eventId.longValue());

            if (eventRatingData.isEmpty()) {
                result.add(EventRatingDto.builder()
                        .eventId(eventId)
                        .likes(0)
                        .dislikes(0)
                        .rating(rating)
                        .build());
            } else {
                Object[] fullData = eventRatingData.get(0);
                Integer likes = ((Number) fullData[1]).intValue();
                Integer dislikes = ((Number) fullData[2]).intValue();

                result.add(EventRatingDto.builder()
                        .eventId(eventId)
                        .likes(likes)
                        .dislikes(dislikes)
                        .rating(rating)
                        .build());
            }
        }

        return result;
    }
}