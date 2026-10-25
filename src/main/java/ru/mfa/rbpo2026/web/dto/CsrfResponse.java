package ru.mfa.rbpo2026.web.dto;

public record CsrfResponse(String headerName, String parameterName, String token) {}
