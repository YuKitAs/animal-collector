package yukitas.animal.collector.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotBlank;

import yukitas.animal.collector.model.Album;

public final class CreateAlbumRequest {
    @NotBlank
    private String name;

    @JsonCreator
    public CreateAlbumRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Album.Builder builder() {
        return new Album.Builder();
    }
}
