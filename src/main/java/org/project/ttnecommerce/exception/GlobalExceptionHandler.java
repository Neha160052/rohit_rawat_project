package org.project.ttnecommerce.exception;

import lombok.extern.slf4j.Slf4j;
import org.project.ttnecommerce.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        log.warn("Email already exists: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(GstAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleGstExists(GstAlreadyExistsException ex) {
        log.warn("GST already exists: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CompanyAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleCompanyExists(CompanyAlreadyExistsException ex) {
        log.warn("Company already exists: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiResponse> handlePasswordMismatch(PasswordMismatchException ex) {
        log.warn("Password mismatch: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountAlreadyActivatedException.class)
    public ResponseEntity<ApiResponse> handleAccountAlreadyActivated(AccountAlreadyActivatedException ex) {
        log.warn("Account already activated: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<ApiResponse> handleAccountNotActivated(AccountNotActivatedException ex) {
        log.warn("Account not activated: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse> handleAccountLocked(AccountLockedException ex) {
        log.warn("Account locked: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("Invalid credentials: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ApiResponse> handleTokenRefresh(TokenRefreshException ex) {
        log.warn("Token refresh failed: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ApiResponse response = new ApiResponse(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidToken.class)
    public ResponseEntity<ApiResponse> handleInvalidToken(InvalidToken ex) {
        log.warn("Invalid token used: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse> handleInvalidRequest(InvalidRequestException ex) {
        log.warn("Invalid request: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponse> handleInvalidInput(InvalidInputException ex) {
        log.warn("Invalid input: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getParameterName());
        ApiResponse response = new ApiResponse("uuid is not present");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        log.warn("Type mismatch error: {}", ex.getMessage());

        if (ex.getRequiredType() != null && ex.getRequiredType().equals(UUID.class)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Invalid category id"));
        }

        return ResponseEntity.badRequest()
                .body(new ApiResponse("Invalid request parameter"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed");

        log.warn("Validation failed: {}", message);

        return new ResponseEntity<>(new ApiResponse(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleJsonError(HttpMessageNotReadableException ex) {

        String message = ex.getMostSpecificCause().getMessage();

        log.warn("Malformed JSON request: {}", message);

        if (message.contains("UUID")) {
            return new ResponseEntity<>(
                    new ApiResponse("Invalid UUID format"),
                    HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
                new ApiResponse("Malformed JSON request"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleUnexpectedError(Exception ex) {

        log.error("Unexpected server error occurred", ex);

        return new ResponseEntity<>(
                new ApiResponse("Something went wrong"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}