package yukitas.animal.collector.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotBlank;

import yukitas.animal.collector.model.Animal;

public final class CreateAnimalRequest {
    @NotBlank
    private String name;

    private String[] tags;

    @JsonCreator
    public CreateAnimalRequest(String name, String[] tags) {
        this.name = name;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public String[] getTags() {
        return tags;
    }

    public static Animal.Builder builder() {
        return new Animal.Builder();
    }
}
