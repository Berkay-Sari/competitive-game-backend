package com.dreamgames.backendengineeringcasestudy.exception;

import com.dreamgames.backendengineeringcasestudy.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ControllerAdvice
public class CustomExceptionHandler {

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_STATUS_MAP = Stream.of(
            Map.entry(NoActiveTournamentException.class, HttpStatus.NOT_FOUND),
            Map.entry(UserAlreadyParticipantException.class, HttpStatus.BAD_REQUEST),
            Map.entry(UserNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(UnclaimedRewardException.class, HttpStatus.BAD_REQUEST),
            Map.entry(MinimumLevelRequirementException.class, HttpStatus.BAD_REQUEST),
            Map.entry(InsufficientCoinsException.class, HttpStatus.BAD_REQUEST),
            Map.entry(UserNeverParticipatedException.class, HttpStatus.BAD_REQUEST),
            Map.entry(RewardAlreadyClaimedException.class, HttpStatus.BAD_REQUEST),
            Map.entry(NoRewardForRankException.class, HttpStatus.BAD_REQUEST),
            Map.entry(TournamentNotFinishedException.class, HttpStatus.BAD_REQUEST),
            Map.entry(GroupNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(NotEnoughParticipantsException.class, HttpStatus.BAD_REQUEST)
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

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
