package ru.practicum.main.service.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.service.event.dto.EventFullDto;
import ru.practicum.main.service.event.dto.EventShortDto;
import ru.practicum.main.service.event.dto.NewEventDto;
import ru.practicum.main.service.event.dto.UpdateEventUserRequest;
import ru.practicum.main.service.event.service.EventService;
import ru.practicum.main.service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.service.request.dto.ParticipationRequestDto;
import ru.practicum.main.service.request.service.RequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class PrivateEventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto dto) {
        log.info("POST /users/{}/events - создание события: {}", userId, dto);
        return eventService.createEvent(userId, dto);
    }

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info("GET /users/{}/events - получение событий пользователя", userId);
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEventById(@PathVariable Long userId,
                                         @PathVariable Long eventId) {
        log.info("GET /users/{}/events/{} - получение события", userId, eventId);
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventUserRequest dto) {
        log.info("PATCH /users/{}/events/{} - обновление события: {}", userId, eventId, dto);
        return eventService.updateUserEvent(userId, eventId, dto);
    }
    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        log.info("GET /users/{}/events/{}/requests", userId, eventId);
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestsStatus(@PathVariable Long userId,
                                                                    @PathVariable Long eventId,
                                                                    @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("PATCH /users/{}/events/{}/requests: {}", userId, eventId, updateRequest);
        return requestService.updateEventRequestsStatus(userId, eventId, updateRequest);
    }
    private final RequestService requestService;
}
