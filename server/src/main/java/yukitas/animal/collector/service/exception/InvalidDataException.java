package yukitas.animal.collector.service.exception;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String fieldName, String value) {
        super(String.format("Provided '%s' contains invalid value(s): %s", fieldName.toLowerCase(), value));
    }
}