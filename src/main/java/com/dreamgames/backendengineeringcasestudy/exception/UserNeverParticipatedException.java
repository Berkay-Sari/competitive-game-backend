package com.dreamgames.backendengineeringcasestudy.exception;

public class UserNeverParticipatedException extends RuntimeException {
    public UserNeverParticipatedException() {
        super("User never participated in a tournament");
    }
}
