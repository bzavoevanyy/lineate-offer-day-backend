package com.example.offerdaysongs.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ErrorResponse {
    private final ZonedDateTime timestamp;
    private final int status;
    private final String message;
}
