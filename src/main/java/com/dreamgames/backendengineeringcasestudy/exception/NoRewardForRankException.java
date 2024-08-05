package com.dreamgames.backendengineeringcasestudy.exception;

public class NoRewardForRankException extends RuntimeException {
    public NoRewardForRankException(int rank) {
        super("No reward for the rank: " + rank);
    }
}
