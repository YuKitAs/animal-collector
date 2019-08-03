package yukitas.animal.collector.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

import yukitas.animal.collector.service.utility.Category;

public final class CreatePhotoResponse {
    private final UUID id;

    private final Category detectedCategory;

    @JsonCreator
    private CreatePhotoResponse(@JsonProperty("id") UUID id,
            @JsonProperty("detected_category") Category detectedCategory) {
        this.id = id;
        this.detectedCategory = detectedCategory;
    }

    public UUID getId() {
        return id;
    }

    public Category getDetectedCategory() {
        return detectedCategory;
    }

    public static class Builder {
        private UUID id;
        private Category category;

        public Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder setCategory(Category category) {
            this.category = category;
            return this;
        }

        public CreatePhotoResponse build() {
            return new CreatePhotoResponse(id, category);
        }
    }
}
