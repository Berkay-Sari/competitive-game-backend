package com.dreamgames.backendengineeringcasestudy.exception;

public class MinimumLevelRequirementException extends RuntimeException {
    public MinimumLevelRequirementException(int minimumLevel) {
        super("Must be at least level " + minimumLevel);
    }
}