package com.plato.server.core.services;

import com.plato.server.core.models.Event;
import com.plato.server.core.models.Match;
import com.plato.server.repository.MatchRepository;

import java.util.UUID;

public class MatchService {
    private final MatchRepository matchRepository;
    private final EventService eventService;

    public MatchService(MatchRepository matchRepository, EventService eventService) {
        this.matchRepository = matchRepository;
        this.eventService = eventService;
    }

    public void insertMatch(Match match) {
        match.setScore(1);
        match.setId(UUID.randomUUID().toString());
        match.setPlayedAt((int) (System.currentTimeMillis() / 1000));

        Event activeEvent = this.eventService.getCurrentActiveEventForGame(match.getGameName());
        if(activeEvent != null) {
            match.setEventId(activeEvent.getId());
            match.setScore(match.getScore() * activeEvent.getScoreMultiplier());
        }

        this.matchRepository.insertMatch(match);
    }

    public long getPlayerWinCountByGameName(String playerId, String gameName) {
        return this.matchRepository.getPlayerWinCountByGameName(playerId, gameName);
    }

    public int getPlayerScoreByGameName(String playerId, String gameName) {
        return this.matchRepository.getPlayerScoreByGameName(playerId, gameName);
    }
}
