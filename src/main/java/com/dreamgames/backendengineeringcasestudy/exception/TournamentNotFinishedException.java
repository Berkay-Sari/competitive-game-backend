package com.dreamgames.backendengineeringcasestudy.exception;

public class TournamentNotFinishedException extends RuntimeException {
    public TournamentNotFinishedException() {
        super("Tournament is not finished yet");
    }
}
