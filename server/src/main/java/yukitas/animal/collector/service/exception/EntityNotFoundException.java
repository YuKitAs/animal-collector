package yukitas.animal.collector.service.exception;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, UUID id) {
        super(String.format("%s not found by id=%s", entityName.toUpperCase(), id));
    }
}
