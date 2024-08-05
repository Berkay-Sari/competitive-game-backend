package com.dreamgames.backendengineeringcasestudy.exception;

public class UnclaimedRewardException extends RuntimeException {
    public UnclaimedRewardException() {
        super("User has unclaimed reward");
    }
}
