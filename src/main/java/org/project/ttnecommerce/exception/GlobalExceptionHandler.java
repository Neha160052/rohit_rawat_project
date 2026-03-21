package org.project.ttnecommerce.exception;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.ApiResponse;
import org.project.ttnecommerce.i18n.MessageTranslator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.UUID;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageTranslator translator;

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiResponse> handlePasswordMismatch(PasswordMismatchException ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountAlreadyActivatedException.class)
    public ResponseEntity<ApiResponse> handleAccountAlreadyActivated(AccountAlreadyActivatedException ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<ApiResponse> handleAccountNotActivated(AccountNotActivatedException ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(GstAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleGstExists(GstAlreadyExistsException ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CompanyAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleCompanyExists(CompanyAlreadyExistsException ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidToken.class)
    public ResponseEntity<ApiResponse> handleInvalidToken(InvalidToken ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ApiResponse> handleTokenRefresh(TokenRefreshException ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse> handleAccountLocked(AccountLockedException ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse> handleInvalidRequest(InvalidRequestException ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponse> handleInvalidInput(InvalidInputException ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse(translator.translate(ex.getMessage())), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse> handleNoResourceFound(NoResourceFoundException ex) {

        return new ResponseEntity<>(
                new ApiResponse(translator.get("error.api.not.found")),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleJsonParse(HttpMessageNotReadableException ex) {

        String message = translator.get("error.request.invalid");

        if (ex.getMessage() != null && ex.getMessage().contains("UUID")) {
            message = translator.get("error.uuid.invalid");
        }

        return new ResponseEntity<>(
                new ApiResponse(message),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        if (ex.getRequiredType() == UUID.class) {
            return new ResponseEntity<>(
                    new ApiResponse(translator.get("error.uuid.invalid")),
                    HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
                new ApiResponse(translator.get("error.request.parameter.invalid")),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    String fieldName = translator.getOrDefault("field." + error.getField(), error.getField());
                    return fieldName + ": " + error.getDefaultMessage();
                })
                .toList();

        return new ResponseEntity<>(
                new ApiResponse(translator.get("validation.failed"), errors),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParams(MissingServletRequestParameterException ex) {

        String field = translator.getOrDefault("field." + ex.getParameterName(), ex.getParameterName());
        String message = field + ": " + translator.get("validation.required");

        return new ResponseEntity<>(
                new ApiResponse(message, null),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneric(Exception ex) {

        return new ResponseEntity<>(
                new ApiResponse(translator.get("error.internal")),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}