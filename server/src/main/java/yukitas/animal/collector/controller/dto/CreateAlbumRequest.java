package yukitas.animal.collector.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public final class CreateAlbumRequest {
    @NotBlank
    private String name;

    @JsonCreator
    public CreateAlbumRequest(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
