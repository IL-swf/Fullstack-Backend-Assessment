package com.il.librarybackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException (Exception e, WebRequest request) {
        return ResponseEntity.badRequest().body(generateErrorResponse(e, request, "No Element at that address."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException (Exception e, WebRequest request) {
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(generateErrorResponse(e, request, "The data you provided wasn't valid."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException (Exception e, WebRequest request) {
        return ResponseEntity.badRequest().body(generateErrorResponse(e, request, "That didn't seem to work. Try something different."));
    }

    private String generateErrorResponse(Exception e, WebRequest request, String message) {

        return String.format("""
                {
                "timestamp": %tD,
                "request": "%s",
                "error": "%s",
                "message": "%s"
                }
                """,
                new Timestamp(Instant.now().toEpochMilli()),
                request.getDescription(false),
                e.getMessage(),
                message);
    }

}
