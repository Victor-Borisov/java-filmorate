package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Month;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {
    static final LocalDate FIRST_CINEMA_DATE = LocalDate.of(1895, Month.DECEMBER,28);
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(FIRST_CINEMA_DATE);
    }
}