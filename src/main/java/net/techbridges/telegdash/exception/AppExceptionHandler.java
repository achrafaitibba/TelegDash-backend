package net.techbridges.telegdash.exception;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {RequestException.class})
    public ResponseEntity<Object> handleApiRequestException(RequestException e){
        ApiException apiException = new ApiException(
                e.getMessage(),
                e.getHttpStatus(),
                e.getHttpStatus().value(),
                ZonedDateTime.now(ZoneId.of("GMT"))
        );
        return new ResponseEntity<>(apiException, e.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String message = error.getDefaultMessage();
            errorMessage.append(message).append(", ");
        });

        errorMessage.replace(errorMessage.length() - 2, errorMessage.length(), ".");
        String finalErrorMessage = errorMessage.toString();

        ApiException apiException = new ApiException(
                finalErrorMessage,
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                ZonedDateTime.now(ZoneId.of("GMT"))
        );

        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }
}
