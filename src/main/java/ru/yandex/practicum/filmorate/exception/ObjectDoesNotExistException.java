package ru.yandex.practicum.filmorate.exception;

public class ObjectDoesNotExistException extends RuntimeException {
    public ObjectDoesNotExistException(String message) {
        super(message);
    }
}
