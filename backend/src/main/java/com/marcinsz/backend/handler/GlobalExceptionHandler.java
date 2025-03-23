package com.marcinsz.backend.handler;

import com.marcinsz.backend.exception.*;
import com.marcinsz.backend.response.ExceptionResponse;
import com.marcinsz.backend.response.ValidationErrorsResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IncorrectLoginOrPasswordException.class)
    public ResponseEntity<ExceptionResponse> incorrectLoginOrPasswordExceptionHandler(Exception ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                buildBodyExceptionResponse(HttpStatus.UNAUTHORIZED, ex)
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> userNotFoundExceptionHandler(Exception ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildBodyExceptionResponse(HttpStatus.NOT_FOUND, ex)
        );
    }

    @ExceptionHandler(UserActivationTokenNotFoundException.class)
    public ResponseEntity<ExceptionResponse> userActivationTokenNotFoundExceptionHandler(Exception ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildBodyExceptionResponse(HttpStatus.NOT_FOUND, ex)
        );
    }

    @ExceptionHandler(UserActivationTokenExpiredException.class)
    public ResponseEntity<ExceptionResponse> userActivationTokenExpiredExceptionHandler(Exception ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                buildBodyExceptionResponse(HttpStatus.FORBIDDEN, ex)
        );
    }

    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<ExceptionResponse> userNotActivatedExceptionHandler(Exception ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                buildBodyExceptionResponse(HttpStatus.UNAUTHORIZED, ex)
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionResponse> invalidTokenExceptionHandler(Exception ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                buildBodyExceptionResponse(HttpStatus.FORBIDDEN, ex)
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionResponse> usernameNotFoundExceptionHandler(Exception ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildBodyExceptionResponse(HttpStatus.NOT_FOUND,ex));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> userAlreadyExistsExceptionHandler(Exception ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                buildBodyExceptionResponse(HttpStatus.CONFLICT, ex));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ExceptionResponse> securityExceptionHandler(Exception ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                buildBodyExceptionResponse(HttpStatus.FORBIDDEN, ex));
    }

    @ExceptionHandler(GuaranteeNotFoundException.class)
    public ResponseEntity<ExceptionResponse> guaranteeNotFoundExceptionHandler(Exception ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildBodyExceptionResponse(HttpStatus.NOT_FOUND, ex));
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errorMessages = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        String errorMessage = errorMessages.isEmpty() ? "Validation error occurred" : errorMessages.getFirst();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildBodyExceptionResponse(
                        errorMessage
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorsResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();
        ex.getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(),fieldError.getDefaultMessage()));
        return ResponseEntity.badRequest().body(
                ValidationErrorsResponse.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .errors(errors)
                        .build());
    }

    private ExceptionResponse buildBodyExceptionResponse(HttpStatus httpStatus, Exception ex) {
        return ExceptionResponse.builder()
                .statusCode(httpStatus.value())
                .message(ex.getMessage())
                .build();
    }

    private ExceptionResponse buildBodyExceptionResponse(String message) {
        return ExceptionResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();
    }
}
