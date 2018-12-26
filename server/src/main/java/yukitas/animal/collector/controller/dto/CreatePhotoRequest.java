package yukitas.animal.collector.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import yukitas.animal.collector.model.Location;
import yukitas.animal.collector.model.Photo;

public final class CreatePhotoRequest {
    @NotNull
    private final Set<UUID> animalIds;

    @NotNull
    private final Set<UUID> albumIds;

    @NotNull
    private final byte[] content;

    private final String description;
    private final Location location;

    @JsonCreator
    public CreatePhotoRequest(@NotNull Set<UUID> animalIds, @NotNull Set<UUID> albumIds, byte[] content,
            String description, Location location) {
        this.animalIds = animalIds;
        this.albumIds = albumIds;
        this.content = content;
        this.description = description;
        this.location = location;
    }

    public Set<UUID> getAnimalIds() {
        return animalIds;
    }

    public Set<UUID> getAlbumIds() {
        return albumIds;
    }

    public byte[] getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public Location getLocation() {
        return location;
    }

    public static Photo.Builder builder() {
        return new Photo.Builder();
    }
}
