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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.main.service.rating.dto.EventRatingDto;
import ru.practicum.main.service.rating.dto.UserRatingDto;
import ru.practicum.main.service.rating.service.RatingService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/events/{eventId}/like")
    @ResponseStatus(HttpStatus.OK)
    public EventRatingDto addLike(@PathVariable Long eventId,
                                  @RequestHeader("X-User-Id") Long userId) {
        log.info("POST /events/{}/like от пользователя {}", eventId, userId);
        return ratingService.addLike(eventId, userId);
    }

    @PostMapping("/events/{eventId}/dislike")
    @ResponseStatus(HttpStatus.OK)
    public EventRatingDto addDislike(@PathVariable Long eventId,
                                     @RequestHeader("X-User-Id") Long userId) {
        log.info("POST /events/{}/dislike от пользователя {}", eventId, userId);
        return ratingService.addDislike(eventId, userId);
    }

    @DeleteMapping("/events/{eventId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable Long eventId,
                           @RequestHeader("X-User-Id") Long userId) {
        log.info("DELETE /events/{}/like от пользователя {}", eventId, userId);
        ratingService.removeLike(eventId, userId);
    }

    @DeleteMapping("/events/{eventId}/dislike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDislike(@PathVariable Long eventId,
                              @RequestHeader("X-User-Id") Long userId) {
        log.info("DELETE /events/{}/dislike от пользователя {}", eventId, userId);
        ratingService.removeDislike(eventId, userId);
    }

    @GetMapping("/events/{eventId}/rating")
    @ResponseStatus(HttpStatus.OK)
    public EventRatingDto getEventRating(@PathVariable Long eventId) {
        log.info("GET /events/{}/rating", eventId);
        return ratingService.getEventRating(eventId);
    }

    @GetMapping("/users/{userId}/rating")
    @ResponseStatus(HttpStatus.OK)
    public UserRatingDto getUserRating(@PathVariable Long userId) {
        log.info("GET /users/{}/rating", userId);
        return ratingService.getUserRating(userId);
    }

    @GetMapping("/events/top")
    @ResponseStatus(HttpStatus.OK)
    public List<EventRatingDto> getTopRatedEvents(
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /events/top с size={}", size);
        return ratingService.getTopRatedEvents(size);
    }
}