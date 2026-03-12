package ru.practicum.main.service.rating.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.service.event.model.Event;
import ru.practicum.main.service.event.repository.EventRepository;
import ru.practicum.main.service.exception.EventNotFoundException;
import ru.practicum.main.service.exception.OperationConditionsNotMetException;
import ru.practicum.main.service.exception.UserNotFoundException;
import ru.practicum.main.service.rating.dto.EventRatingResponseDto;
import ru.practicum.main.service.rating.dto.EventRatingStatsDto;
import ru.practicum.main.service.rating.dto.EventRatingListDto;
import ru.practicum.main.service.rating.dto.EventRatingTopDto;
import ru.practicum.main.service.rating.mapper.RatingMapper;
import ru.practicum.main.service.rating.model.EventRating;
import ru.practicum.main.service.rating.repository.EventRatingRepository;
import ru.practicum.main.service.rating.service.RatingService;
import ru.practicum.main.service.user.model.User;
import ru.practicum.main.service.user.repository.UserRepository;
import ru.practicum.main.service.request.repository.RequestRepository;
import ru.practicum.main.service.request.model.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final EventRatingRepository ratingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public EventRatingResponseDto addLike(Long userId, Long eventId) {
        return addRating(userId, eventId, EventRating.RatingType.LIKE);
    }

    @Override
    @Transactional
    public EventRatingResponseDto addDislike(Long userId, Long eventId) {
        return addRating(userId, eventId, EventRating.RatingType.DISLIKE);
    }

    private EventRatingResponseDto addRating(Long userId, Long eventId, EventRating.RatingType type) {
        log.info("Добавление оценки {} для события id={} пользователем id={}", type, eventId, userId);

        User user = userRepository.findById(userId.intValue())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + userId + " не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Событие с id=" + eventId + " не найдено"));

        if (event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new OperationConditionsNotMetException("Нельзя оценивать событие, которое ещё не состоялось");
        }

        boolean userAttended = requestRepository.existsByEventIdAndRequesterIdAndStatus(
                eventId.intValue(),
                userId.intValue(),
                RequestStatus.CONFIRMED);

        if (!userAttended) {
            throw new OperationConditionsNotMetException("Пользователь не посещал данное событие");
        }

        EventRating existingRating = ratingRepository.findByEventIdAndUserId(eventId, userId).orElse(null);

        if (existingRating != null) {
            if (existingRating.getRatingType() == type) {
                throw new OperationConditionsNotMetException("Вы уже оценили это событие");
            }
            existingRating.setRatingType(type);
            existingRating.setCreated(LocalDateTime.now());
            ratingRepository.save(existingRating);
            log.debug("Оценка обновлена: {}", existingRating);
            return RatingMapper.toResponseDto(existingRating);
        }

        EventRating newRating = RatingMapper.toEntity(eventId, userId, type);
        newRating = ratingRepository.save(newRating);
        log.debug("Создана новая оценка: {}", newRating);

        return RatingMapper.toResponseDto(newRating);
    }

    @Override
    @Transactional
    public void deleteRating(Long userId, Long eventId) {
        log.info("Удаление оценки для события id={} пользователем id={}", eventId, userId);

        EventRating rating = ratingRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new OperationConditionsNotMetException(
                        "Оценка не найдена для события id=" + eventId + " и пользователя id=" + userId));

        ratingRepository.delete(rating);
        log.debug("Оценка удалена");
    }

    @Override
    public EventRatingStatsDto getEventRatingStats(Long eventId) {
        log.info("Получение статистики рейтинга события id={}", eventId);

        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Событие с id=" + eventId + " не найдено");
        }

        List<Object[]> ratingData = ratingRepository.getRatingStatsByEventId(eventId);

        if (ratingData.isEmpty()) {
            return RatingMapper.toStatsDto(eventId, 0L, 0L);
        }

        Object[] data = ratingData.get(0);
        Long likes = ((Number) data[1]).longValue();
        Long dislikes = ((Number) data[2]).longValue();

        return RatingMapper.toStatsDto(eventId, likes, dislikes);
    }

    @Override
    public EventRatingListDto getUserRatings(Long userId, String rating, int from, int size) {
        log.info("Получение оценок пользователя id={}, rating={}, from={}, size={}", userId, rating, from, size);

        if (!userRepository.existsById(userId.intValue())) {  // <--- ИСПРАВЛЕНО
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<EventRating> ratings;
        long totalElements;

        if (rating == null || rating.isEmpty()) {
            ratings = ratingRepository.findByUserIdOrderByCreatedDesc(userId, pageable);
            totalElements = ratingRepository.countByUserId(userId);
        } else if ("like".equalsIgnoreCase(rating)) {
            ratings = ratingRepository.findByUserIdAndRatingTypeOrderByCreatedDesc(
                    userId, EventRating.RatingType.LIKE, pageable);
            totalElements = ratingRepository.countByUserIdAndRatingType(userId, EventRating.RatingType.LIKE);
        } else if ("dislike".equalsIgnoreCase(rating)) {
            ratings = ratingRepository.findByUserIdAndRatingTypeOrderByCreatedDesc(
                    userId, EventRating.RatingType.DISLIKE, pageable);
            totalElements = ratingRepository.countByUserIdAndRatingType(userId, EventRating.RatingType.DISLIKE);
        } else {
            throw new OperationConditionsNotMetException("Параметр rating должен быть 'like' или 'dislike'");
        }

        List<EventRatingResponseDto> ratingDtos = RatingMapper.toResponseDtoList(ratings);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return EventRatingListDto.builder()
                .ratings(ratingDtos)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(from / size)
                .pageSize(size)
                .build();
    }

    @Override
    public List<EventRatingTopDto> getTopRatedEvents(int from, int size, String order) {
        log.info("Получение топа событий, from={}, size={}, order={}", from, size, order);

        Sort.Direction direction;
        if (order == null || "DESC".equalsIgnoreCase(order)) {
            direction = Sort.Direction.DESC;
        } else if ("ASC".equalsIgnoreCase(order)) {
            direction = Sort.Direction.ASC;
        } else {
            throw new OperationConditionsNotMetException("Параметр order должен быть 'ASC' или 'DESC'");
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(direction, "rating"));
        List<Object[]> topEvents = ratingRepository.findTopRatedEvents(pageable);

        return RatingMapper.toTopDtoList(topEvents);
    }
}