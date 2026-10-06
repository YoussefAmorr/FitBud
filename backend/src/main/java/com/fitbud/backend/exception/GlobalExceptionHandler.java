package com.fitbud.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.fitbud.backend.exception.FoodNotFoundException;
import com.fitbud.backend.exception.FoodLogNotFoundException;
import com.fitbud.backend.exception.NutritionGoalNotFoundException;
import com.fitbud.backend.exception.DuplicateEmailException;
import com.fitbud.backend.exception.InvalidCredentialsException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEmail(
            DuplicateEmailException exception) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(NutritionGoalAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleNutritionGoalAlreadyExists(
            NutritionGoalAlreadyExistsException exception) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
    @ExceptionHandler(UserProfileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserProfileNotFound(
            UserProfileNotFoundException exception) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
    @ExceptionHandler(FoodNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFoodNotFound(
            FoodNotFoundException exception) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
    @ExceptionHandler(FoodLogNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFoodLogNotFound(
            FoodLogNotFoundException exception) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
    @ExceptionHandler(NutritionGoalNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNutritionGoalNotFound(
            NutritionGoalNotFoundException exception) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
            InvalidCredentialsException exception) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

}