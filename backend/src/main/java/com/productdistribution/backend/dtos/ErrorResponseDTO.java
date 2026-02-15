package com.productdistribution.backend.dtos;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
    Integer statusCode,
    String message,
    LocalDateTime timestamp,
    String path
) {
    public ErrorResponseDTO(Integer statusCode, String message, String path) {
        this(statusCode, message, LocalDateTime.now(), path);
    }
}