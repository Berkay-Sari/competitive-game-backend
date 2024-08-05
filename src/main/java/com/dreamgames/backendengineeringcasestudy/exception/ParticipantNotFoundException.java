package com.dreamgames.backendengineeringcasestudy.exception;

public class ParticipantNotFoundException extends RuntimeException {
    public ParticipantNotFoundException(Long userId, Long tournamentId) {
        super(String.format("TournamentParticipant not found for user ID: %d and tournament ID: %d", userId, tournamentId));
    }
}
