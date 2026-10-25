package ru.mfa.rbpo2026.common;

/** Ресурс с указанным идентификатором отсутствует в хранилище. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
