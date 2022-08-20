package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(final ValidationException e) {
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleFilmDoesNotExistException(final ObjectDoesNotExistException e) {
        return e.getMessage();
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleThrowable(final Throwable e) {
        return e.getMessage();
    }
}
