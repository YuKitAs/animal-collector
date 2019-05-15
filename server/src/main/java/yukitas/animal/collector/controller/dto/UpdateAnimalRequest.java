package yukitas.animal.collector.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateAnimalRequest {
    private String name;

    private String[] tags;

    @JsonCreator
    public UpdateAnimalRequest(@JsonProperty("name") String name, @JsonProperty("tags") String[] tags) {
        this.name = name;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public String[] getTags() {
        return tags;
    }
}
