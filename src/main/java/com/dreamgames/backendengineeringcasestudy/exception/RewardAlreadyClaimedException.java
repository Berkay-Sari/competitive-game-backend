package com.dreamgames.backendengineeringcasestudy.exception;

public class RewardAlreadyClaimedException extends RuntimeException {
    public RewardAlreadyClaimedException() {
        super("User has already claimed the reward");
    }
}
