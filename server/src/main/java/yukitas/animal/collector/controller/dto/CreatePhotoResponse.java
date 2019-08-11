package yukitas.animal.collector.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

import yukitas.animal.collector.service.utility.AnimalClass;

public final class CreatePhotoResponse {
    private final UUID id;

    private final AnimalClass recognizedCategory;

    @JsonCreator
    private CreatePhotoResponse(@JsonProperty("id") UUID id,
            @JsonProperty("recognized_category") AnimalClass recognizedCategory) {
        this.id = id;
        this.recognizedCategory = recognizedCategory;
    }

    public UUID getId() {
        return id;
    }

    public AnimalClass getRecognizedCategory() {
        return recognizedCategory;
    }

    public static class Builder {
        private UUID id;
        private AnimalClass category;

        public Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder setCategory(AnimalClass category) {
            this.category = category;
            return this;
        }

        public CreatePhotoResponse build() {
            return new CreatePhotoResponse(id, category);
        }
    }
}
