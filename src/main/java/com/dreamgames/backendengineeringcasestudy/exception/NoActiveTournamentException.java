package com.dreamgames.backendengineeringcasestudy.exception;

public class NoActiveTournamentException extends RuntimeException {
    public NoActiveTournamentException() {
        super("No active tournament");
    }
}