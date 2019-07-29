package yukitas.animal.collector.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import yukitas.animal.collector.model.Location;
import yukitas.animal.collector.model.Photo;

public class GetPhotoResponse {
    private UUID id;

    private byte[] content;

    private String description;

    private OffsetDateTime createdAt;

    private Location location;

    @JsonCreator
    private GetPhotoResponse(@JsonProperty("id") UUID id, @JsonProperty("content") byte[] content,
            @JsonProperty("description") String description, @JsonProperty("createdAt") OffsetDateTime createdAt,
            @JsonProperty("location") Location location) {
        this.id = id;
        this.content = content;
        this.description = description;
        this.createdAt = createdAt;
        this.location = location;
    }

    public UUID getId() {
        return id;
    }

    public byte[] getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public Location getLocation() {
        return location;
    }

    public static GetPhotoResponse from(Photo photo) {
        OffsetDateTime createdAt = photo.getCreatedAt()
                .toInstant()
                .atOffset(ZoneOffset.ofTotalSeconds(photo.getCreatedAtOffset()));
        return new GetPhotoResponse(photo.getId(), photo.getContent(), photo.getDescription(), createdAt,
                photo.getLocation());
    }
}
