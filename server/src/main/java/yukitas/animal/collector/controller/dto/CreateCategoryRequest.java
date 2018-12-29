package yukitas.animal.collector.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

import yukitas.animal.collector.model.Category;

public final class CreateCategoryRequest {
    @NotBlank
    private final String name;

    @JsonCreator
    public CreateCategoryRequest(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Category.Builder builder() {
        return new Category.Builder();
    }
}
