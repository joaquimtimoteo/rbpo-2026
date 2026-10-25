package ru.mfa.rbpo2026.common;

import java.time.LocalDateTime;
import java.util.List;

/** Единый формат тела ответа при ошибке. */
public record ApiError(LocalDateTime timestamp, int status, String error, List<String> details) {
}
