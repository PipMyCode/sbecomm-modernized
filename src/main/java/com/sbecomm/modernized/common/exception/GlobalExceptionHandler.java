package com.sbecomm.modernized.common.exception;

import com.sbecomm.modernized.cart.application.exception.CartEmptyException;
import com.sbecomm.modernized.catalog.domain.exception.InsufficientStockException;
import com.sbecomm.modernized.order.application.exception.OrderNotFoundException;
import com.sbecomm.modernized.order.domain.exception.InvalidOrderStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
        InvalidOrderStateException.class, 
        InsufficientStockException.class, 
        CartEmptyException.class, 
        IllegalArgumentException.class, 
        IllegalStateException.class
    })
    public ProblemDetail handleDomainAndBadRequestExceptions(RuntimeException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        log.warn("Bad Request (400) at {}: {}", path, ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Bad Request");
        problemDetail.setInstance(URI.create(path));
        return problemDetail;
    }

    @ExceptionHandler({OrderNotFoundException.class})
    public ProblemDetail handleNotFoundExceptions(RuntimeException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        log.warn("Not Found (404) at {}: {}", path, ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setInstance(URI.create(path));
        return problemDetail;
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
            
        log.warn("Validation failed (400) at {}: {}", path, errors);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setTitle("Validation Error");
        problemDetail.setInstance(URI.create(path));
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnhandledExceptions(Exception ex, WebRequest request) throws Exception {
        String path = request.getDescription(false).replace("uri=", "");
        
        // Handle Spring Security exceptions explicitly
        if (ex instanceof org.springframework.security.access.AccessDeniedException ||
            ex.getClass().getName().contains("AuthorizationDeniedException")) {
            log.warn("Forbidden (403) at {}: {}", path, ex.getMessage());
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access Denied");
            problemDetail.setTitle("Forbidden");
            problemDetail.setInstance(URI.create(path));
            return problemDetail;
        }
        
        if (ex instanceof org.springframework.security.core.AuthenticationException) {
            log.warn("Unauthorized (401) at {}: {}", path, ex.getMessage());
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Unauthorized");
            problemDetail.setTitle("Unauthorized");
            problemDetail.setInstance(URI.create(path));
            return problemDetail;
        }
        
        // 5xx errors get ERROR level logging with full stack traces
        log.error("Internal Server Error (500) at {}: {}", path, ex.getMessage(), ex);
        
        // Return a generic message so we don't leak internal stack traces to the client
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected internal server error occurred.");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setInstance(URI.create(path));
        return problemDetail;
    }
}
