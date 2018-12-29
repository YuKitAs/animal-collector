package yukitas.animal.collector.service.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExceptionResponseBody {
    private final String errorMessage;

    @JsonCreator
    ExceptionResponseBody(@JsonProperty("message") String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
