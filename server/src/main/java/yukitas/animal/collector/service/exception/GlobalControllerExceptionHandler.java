package yukitas.animal.collector.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponseBody> handleEntityNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(new ExceptionResponseBody(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RequiredDataNotProvidedException.class)
    public ResponseEntity<ExceptionResponseBody> handleRequiredDataNotProvided(RequiredDataNotProvidedException ex) {
        return new ResponseEntity<>(new ExceptionResponseBody(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
