package com.dreamgames.backendengineeringcasestudy.exception;

public class InsufficientCoinsException extends RuntimeException {
    public InsufficientCoinsException(int requiredCoins) {
        super("Must have " + requiredCoins + " coins");
    }
}
