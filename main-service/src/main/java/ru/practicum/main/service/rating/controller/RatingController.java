package ru.practicum.main.service.rating.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.main.service.rating.dto.EventRatingResponseDto;
import ru.practicum.main.service.rating.dto.EventRatingStatsDto;
import ru.practicum.main.service.rating.dto.EventRatingListDto;
import ru.practicum.main.service.rating.dto.EventRatingTopDto;
import ru.practicum.main.service.rating.service.RatingService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/private/events/{userId}/{eventId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public EventRatingResponseDto addLike(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("POST /private/events/{}/{}/like", userId, eventId);
        return ratingService.addLike(userId, eventId);
    }

    @PostMapping("/private/events/{userId}/{eventId}/dislike")
    @ResponseStatus(HttpStatus.CREATED)
    public EventRatingResponseDto addDislike(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("POST /private/events/{}/{}/dislike", userId, eventId);
        return ratingService.addDislike(userId, eventId);
    }

    @DeleteMapping("/private/events/{userId}/{eventId}/deleteRating")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRating(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("DELETE /private/events/{}/{}/deleteRating", userId, eventId);
        ratingService.deleteRating(userId, eventId);
    }

    @GetMapping("/public/events/{eventId}/eventRating")
    @ResponseStatus(HttpStatus.OK)
    public EventRatingStatsDto getEventRatingStats(@PathVariable Long eventId) {
        log.info("GET /public/events/{}/eventRating", eventId);
        return ratingService.getEventRatingStats(eventId);
    }

    @GetMapping("/public/events/{userId}/userRating")
    @ResponseStatus(HttpStatus.OK)
    public EventRatingListDto getUserRatings(
            @PathVariable Long userId,
            @RequestParam(required = false) String rating,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /public/events/{}/userRating?rating={}&from={}&size={}", userId, rating, from, size);
        return ratingService.getUserRatings(userId, rating, from, size);
    }

    @GetMapping("/public/events/rating")
    @ResponseStatus(HttpStatus.OK)
    public List<EventRatingTopDto> getTopRatedEvents(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String order) {
        log.info("GET /public/events/rating?from={}&size={}&order={}", from, size, order);
        return ratingService.getTopRatedEvents(from, size, order);
    }
}