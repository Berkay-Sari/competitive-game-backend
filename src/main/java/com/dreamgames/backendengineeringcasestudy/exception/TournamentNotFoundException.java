package com.dreamgames.backendengineeringcasestudy.exception;

public class TournamentNotFoundException extends RuntimeException {
    public TournamentNotFoundException(Long tournamentId) {
        super(String.format("Tournament not found for ID: %d", tournamentId));
    }
}
