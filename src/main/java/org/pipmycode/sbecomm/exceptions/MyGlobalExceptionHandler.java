package org.pipmycode.sbecomm.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MyGlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed for one or more fields");
        problemDetail.setTitle("Validation Error");

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {

            String fieldName = error instanceof FieldError ?
                               ((FieldError) error).getField() :
                               error.getObjectName();

            String message = error.getDefaultMessage();
            errors.putIfAbsent(fieldName, message);
        });

        problemDetail.setProperty("invalid_fields", errors);
        return problemDetail;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Resource Not Found");

        problemDetail.setProperty("resourceName", e.getResourceName());
        problemDetail.setProperty("fieldName", e.getFieldName());
        problemDetail.setProperty("fieldValue", e.getFieldValue());
        return problemDetail;

    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ProblemDetail handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problemDetail.setTitle("Resource Conflict");

        problemDetail.setProperty("resourceName", e.getResourceName());
        problemDetail.setProperty("fieldName", e.getFieldName());
        problemDetail.setProperty("fieldValue", e.getFieldValue());

        return problemDetail;
    }
}
