package ru.practicum.main.service.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.service.event.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
}