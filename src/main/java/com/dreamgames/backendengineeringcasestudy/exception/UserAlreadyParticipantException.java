package com.dreamgames.backendengineeringcasestudy.exception;

public class UserAlreadyParticipantException extends RuntimeException {
    public UserAlreadyParticipantException() {
        super("User is already a participant");
    }
}