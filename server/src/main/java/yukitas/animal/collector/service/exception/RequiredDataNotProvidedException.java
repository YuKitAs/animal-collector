package yukitas.animal.collector.service.exception;

public class RequiredDataNotProvidedException extends RuntimeException {
    public RequiredDataNotProvidedException(String fieldName) {
        super("Required but not provided: " + fieldName.toLowerCase());
    }
}
