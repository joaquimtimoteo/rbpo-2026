package ru.mfa.rbpo2026.common;

/** Запрос корректен по форме, но нарушает правила предметной области. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
