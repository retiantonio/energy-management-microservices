package com.retiantonio.authentication_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private Map<String, String> fieldErrors; // for validation errors
    private LocalDateTime timestamp = LocalDateTime.now();
}