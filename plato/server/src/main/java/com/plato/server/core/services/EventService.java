package com.plato.server.core.services;

import com.plato.server.core.models.Event;
import com.plato.server.repository.EventRepository;

import java.util.UUID;

public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void insertEvent(Event event) {
        event.setId(UUID.randomUUID().toString());
        this.eventRepository.insertEvent(event);
    }

    public Event getCurrentActiveEventForGame(String associatedGame) {
        return this.eventRepository.getCurrentActiveEventForGame(associatedGame);
    }
}
