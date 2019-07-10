package yukitas.animal.collector.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.NotNull;

public final class UpdatePhotoRequest {
    @NotNull
    private final Set<UUID> animalIds;

    @NotNull
    private final Set<UUID> albumIds;

    @NotNull
    private final String description;

    @JsonCreator
    public UpdatePhotoRequest(@JsonProperty("animal_ids") Set<UUID> animalIds,
            @JsonProperty("album_ids") Set<UUID> albumIds, @JsonProperty("description") String description) {
        this.animalIds = animalIds;
        this.albumIds = albumIds;
        this.description = description;
    }

    public Set<UUID> getAnimalIds() {
        return animalIds;
    }

    public Set<UUID> getAlbumIds() {
        return albumIds;
    }

    public String getDescription() {
        return description;
    }
}
