package system.task_management.exception;

import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserEntityNotFound.class)
    public ResponseEntity<ErrorCustomResponse> handleUserNotFound(UserEntityNotFound e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorCustomResponse(
                        "Пользователь не найден. Проверьте корректность email.",
                        "USER_NOT_FOUND"));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorCustomResponse> handleJwtException(JwtException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorCustomResponse(
                        "Сессия недействительна или токен повреждён.",
                        "JWT_INVALID_OR_EXPIRED"));
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorCustomResponse> handleTaskNotFound(TaskNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorCustomResponse(
                        "Задача с указанным ID не найдена.",
                        "TASK_NOT_FOUND"));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorCustomResponse> handleEmailAlreadyExists(EmailAlreadyExistsException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorCustomResponse(
                        "Пользователь с таким email уже зарегистрирован.",
                        "EMAIL_ALREADY_EXISTS"));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorCustomResponse> handleUsernameNotFound(UsernameNotFoundException e) {
        String message = e.getMessage();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorCustomResponse(message, "AUTHORIZATION_USERNAME_NOT_FOUND"));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorCustomResponse> handleInvalidRefreshToken(InvalidRefreshTokenException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorCustomResponse(
                        "Неверный refresh токен или email.",
                        "INVALID_REFRESH_TOKEN"));
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<ErrorCustomResponse> handleRefreshTokenExpired(RefreshTokenExpiredException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorCustomResponse(
                        "Refresh токен истёк. Выполните вход заново.",
                        "REFRESH_TOKEN_EXPIRED"));
    }

    @ExceptionHandler(RefreshTokenEntityNotFound.class)
    public ResponseEntity<ErrorCustomResponse> handleRefreshTokenEntityNotFound(RefreshTokenEntityNotFound e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorCustomResponse(
                        "Refresh токен не найден. Выполните вход заново.",
                        "REFRESH_TOKEN_ENTITY_NOT_FOUND"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorCustomResponse> handleValidationError(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Ошибка валидации");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorCustomResponse(message, "VALIDATION_ERROR"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorCustomResponse> handleValidationError(HttpMessageNotReadableException e, HttpServletRequest request) {
        String message = e.getMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorCustomResponse(message, "REQUEST_VALIDATION_ERROR"));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorCustomResponse> handleValidationError(BindException e, HttpServletRequest request) {
        String mesage = e.getMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorCustomResponse(mesage, "REQUEST_ATTRIBUTE_PARAMETRS_OR_BODY_VALIDATION_ERROR"));
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorCustomResponse> handleValidationError(InvalidDataAccessApiUsageException e, HttpServletRequest request) {
        String mesage = e.getLocalizedMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorCustomResponse(mesage, "ENTITY_MAPPING_INVALID"));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorCustomResponse> handleEntityNotFound(EntityNotFoundException e, HttpServletRequest request) {
        String mesage = e.getMessage();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorCustomResponse(mesage, "ENTITY_NOT_FOUND"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorCustomResponse> handleUnhandledExceptions(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorCustomResponse(
                        "Внутренняя ошибка сервера. Мы уже работаем над этим.",
                        "INTERNAL_ERROR"));
    }
}

