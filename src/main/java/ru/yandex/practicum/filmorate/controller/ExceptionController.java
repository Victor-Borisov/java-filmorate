package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(final ConstraintViolationException e) {
        log.debug("ConstraintViolationException: {}", e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(final ValidationException e) {
        log.debug("ValidationException: {}", e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleObjectDoesNotExistException(final ObjectDoesNotExistException e) {
        log.debug("ObjectDoesNotExistException: {}", e.getMessage());
        return e.getMessage();
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleThrowable(final Throwable e) {
        log.debug("Throwable: {}", e.getMessage());
        return e.getMessage();
    }
}
