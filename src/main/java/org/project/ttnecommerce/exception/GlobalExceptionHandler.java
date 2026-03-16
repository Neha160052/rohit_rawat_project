package org.project.ttnecommerce.exception;

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

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(GstAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleGstExists(GstAlreadyExistsException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CompanyAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleCompanyExists(CompanyAlreadyExistsException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiResponse> handlePasswordMismatch(PasswordMismatchException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountAlreadyActivatedException.class)
    public ResponseEntity<ApiResponse> handleAccountAlreadyActivated(AccountAlreadyActivatedException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<ApiResponse> handleAccountNotActivated(AccountNotActivatedException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse> handleAccountLocked(AccountLockedException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ApiResponse> handleTokenRefresh(TokenRefreshException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiResponse response = new ApiResponse(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidToken.class)
    public ResponseEntity<ApiResponse> handleInvalidToken(InvalidToken ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse> handleInvalidRequest(InvalidRequestException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponse> handleInvalidInput(InvalidInputException ex) {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        ApiResponse response = new ApiResponse("uuid is not present");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

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

        return new ResponseEntity<>(new ApiResponse(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleJsonError(HttpMessageNotReadableException ex) {

        String message = ex.getMostSpecificCause().getMessage();

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

        return new ResponseEntity<>(
                new ApiResponse("Something went wrong"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}