package com.brasileiras.ecommerce_api.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // Handler para ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    // Handler para DataConflictException
    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<Object> handleDataConflictException(DataConflictException ex, WebRequest request) {
        return buildErrorResponse(ex, ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    // Handler para BusinessRuleException
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Object> handleBusinessRuleException(BusinessRuleException ex, WebRequest request) {
        return buildErrorResponse(ex, ex.getMessage(), HttpStatus.BAD_REQUEST, request); // Ou HttpStatus.UNPROCESSABLE_ENTITY (422)
    }

    // Handler para MethodArgumentNotValidException (validação de @Valid)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", "Validation Error");

        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (message1, message2) -> message1 + ", " + message2));
        body.put("messages", fieldErrors);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, headers, status);
    }


    // Handler genérico para outras exceções não tratadas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        // É uma boa prática logar a exceção aqui
        logger.error("An unexpected error occurred: ", ex); // Assume que você tem um logger (ex: private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);)
        return buildErrorResponse(ex, "Ocorreu um erro inesperado no servidor.", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception ex, String message, HttpStatus httpStatus, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", httpStatus.value());
        body.put("error", httpStatus.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", "")); // Tira o "uri="

        return new ResponseEntity<>(body, httpStatus);
    }
}
