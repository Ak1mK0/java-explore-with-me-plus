package ru.practicum.main.service.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.service.event.model.Event;
import ru.practicum.main.service.event.model.EventState;
import ru.practicum.main.service.event.repository.EventRepository;
import ru.practicum.main.service.exception.*;
import ru.practicum.main.service.request.dto.ParticipationRequestDto;
import ru.practicum.main.service.request.mapper.RequestMapper;
import ru.practicum.main.service.request.model.ParticipationRequest;
import ru.practicum.main.service.request.model.RequestStatus;
import ru.practicum.main.service.request.repository.RequestRepository;
import ru.practicum.main.service.request.service.RequestService;
import ru.practicum.main.service.user.model.User;
import ru.practicum.main.service.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Integer userId) {
        log.info("Получение заявок пользователя с id: {}", userId);
        checkUserExists(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Integer userId, Integer eventId) {
        log.info("Создание заявки от пользователя {} на событие {}", userId, eventId);

        // Проверка существования пользователя
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        // Проверка существования события
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        // Инициатор не может подать заявку на своё событие
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConditionsNotMetException("Инициатор события не может добавить запрос " +
                    "на участие в своём событии");
        }

        // Событие должно быть опубликовано
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConditionsNotMetException("Нельзя участвовать в неопубликованном событии");
        }

        // Проверка на существующую заявку
        requestRepository.findByEventIdAndRequesterId(eventId, userId)
                .ifPresent(r -> {
                    throw new ConditionsNotMetException("Нельзя добавить повторный запрос на это событие");
                });

        // Проверка лимита участников (если лимит установлен и достигнут)
        if (event.getParticipantLimit() > 0) {
            long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ConditionsNotMetException("Достигнут лимит участников для события");
            }
        }

        // Создание заявки
        ParticipationRequest request = RequestMapper.toNewRequest(event, requester);

        // Если пре-модерация отключена, заявка сразу подтверждается
        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        request = requestRepository.save(request);
        log.info("Заявка создана с id: {}", request.getId());
        return RequestMapper.toDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Integer userId, Integer requestId) {
        log.info("Отмена заявки {} пользователем {}", requestId, userId);

        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + requestId +
                        " не найден или не принадлежит пользователю"));

        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);
        log.info("Заявка {} отменена", requestId);
        return RequestMapper.toDto(request);
    }

    private void checkUserExists(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }
}