package mattia.consiglio.consitech.lms.exceptions;

import lombok.extern.slf4j.Slf4j;
import mattia.consiglio.consitech.lms.payloads.ErrorDTO;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExceptionsHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleBadRequestException(BadRequestException e) {
        if (e.getErrors() != null) {
            return new ErrorDTO(e.getMessage() + ": " + e.getErrors().stream().map(objectError -> objectError.getDefaultMessage()).collect(Collectors.joining(". ")), LocalDateTime.now());
        }
        return new ErrorDTO(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) //401
    public ErrorDTO handleUnauthorizedException(UnauthorizedException e) {
        return new ErrorDTO(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDTO handleAccessDeniedException(AccessDeniedException e) {
        return new ErrorDTO(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String message = e.getMessage();
        if (message != null && message.contains("Required request body is missing")) {
            return new ErrorDTO("The request body is missing.", LocalDateTime.now());
        }
        return new ErrorDTO("The server did not understand the request. Check the correct format of the JSON passed, their keys and values.", LocalDateTime.now());
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleIOException(IOException e) {
        return new ErrorDTO("There is error with the file provided", LocalDateTime.now());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleNoResourceFoundException(NoResourceFoundException e) {
        return new ErrorDTO("The endpoint " + e.getResourcePath() + " not found", LocalDateTime.now());
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorDTO(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handlePropertyReferenceException(PropertyReferenceException e) {
        return new ErrorDTO("The property " + e.getPropertyName() + " is not valid", LocalDateTime.now());
    }

    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleMissingPathVariableException(MissingPathVariableException e) {
        return new ErrorDTO("The path variable " + e.getVariableName() + " is missing", LocalDateTime.now());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ErrorDTO(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorDTO handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return new ErrorDTO("The method " + e.getMethod() + " is not supported", LocalDateTime.now());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleRuntimeException(RuntimeException e) {
        log.error("An error occurred", e);
        return new ErrorDTO("The server encountered an error. The error is reported to the developer.", LocalDateTime.now());
    }
}
