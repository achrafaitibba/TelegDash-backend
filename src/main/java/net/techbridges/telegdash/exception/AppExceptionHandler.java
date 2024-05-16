package net.techbridges.telegdash.exception;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {RequestException.class})
    public ResponseEntity<Object> handleApiRequestException(RequestException e){
        Serializable serializable = e.getMessage();
        ApiException apiException = new ApiException(
                e.getMessage(),
                e.getHttpStatus(),
                e.getHttpStatus().value(),
                ZonedDateTime.now(ZoneId.of("GMT"))
        );
        return new ResponseEntity<>(apiException, e.getHttpStatus());
    }
}
