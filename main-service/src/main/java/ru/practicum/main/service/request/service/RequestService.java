package ru.practicum.main.service.request.service;

import ru.practicum.main.service.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getUserRequests(Integer userId);

    ParticipationRequestDto createRequest(Integer userId, Integer eventId);

    ParticipationRequestDto cancelRequest(Integer userId, Integer requestId);
}