package com.dreamgames.backendengineeringcasestudy.exception;

import com.dreamgames.backendengineeringcasestudy.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_STATUS_MAP = Map.of(
            NoActiveTournamentException.class, HttpStatus.BAD_REQUEST,
            UserAlreadyParticipantException.class, HttpStatus.BAD_REQUEST,
            UserNotFoundException.class, HttpStatus.NOT_FOUND,
            UnclaimedRewardException.class, HttpStatus.BAD_REQUEST,
            MinimumLevelRequirementException.class, HttpStatus.BAD_REQUEST,
            InsufficientCoinsException.class, HttpStatus.BAD_REQUEST,
            UserNeverParticipatedException.class, HttpStatus.BAD_REQUEST,
            RewardAlreadyClaimedException.class, HttpStatus.BAD_REQUEST,
            NoRewardForRankException.class, HttpStatus.BAD_REQUEST,
            TournamentNotFinishedException.class, HttpStatus.BAD_REQUEST
    );

    private ResponseEntity<ErrorResponse> buildResponseEntity(Exception ex, HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).substring(4)
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
        HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        return buildResponseEntity(ex, status, request);
    }
}
