package com.dreamgames.backendengineeringcasestudy.exception;

public class NotEnoughParticipantsException extends RuntimeException {
    public NotEnoughParticipantsException(int participantCount) {
        super("Last tournament's group size was " + participantCount + ", must have 5 participants");
    }
}
