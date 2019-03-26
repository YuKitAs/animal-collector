package yukitas.animal.collector.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

import yukitas.animal.collector.model.Photo;

public final class CreatePhotoRequest {
    @NotNull
    private final byte[] content;

    @JsonCreator
    public CreatePhotoRequest(@JsonProperty("content") byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public static Photo.Builder builder() {
        return new Photo.Builder();
    }
}
