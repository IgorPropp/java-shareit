package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionHandler {

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(final IllegalArgumentException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Unknown state: UNSUPPORTED_STATUS");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<Map<String, String>> handleIllegalAccessException(IllegalAccessException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleNoSuchElementException() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Element not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
