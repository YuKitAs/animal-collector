package yukitas.animal.collector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "photos")
public class Photo {
    private static final Logger LOGGER = LogManager.getLogger(Photo.class);

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "photo_animal", joinColumns = {@JoinColumn(name = "photo_id")}, inverseJoinColumns =
            {@JoinColumn(name = "animal_id")})
    @OrderBy("name")
    @JsonIgnore
    private Set<Animal> animals = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "photo_album", joinColumns = @JoinColumn(name = "photo_id"), inverseJoinColumns =
    @JoinColumn(name = "album_id"))
    @OrderBy("name")
    @JsonIgnore
    private Set<Album> albums = new HashSet<>();

    private byte[] content;

    @Size(max = 255)
    private String description;

    private OffsetDateTime createdAt;

    // PostgreSQL will only store OffsetDateTime in UTC, so a separate column to store the original offset is needed
    // for calculating the original createdAt
    @JsonIgnore
    private Integer createdAtOffset;

    private OffsetDateTime lastModified;

    @Embedded
    private Location location;

    private Photo() {
    }

    private Photo(Set<Animal> animals, Set<Album> albums, byte[] content, String description, OffsetDateTime createdAt,
            Integer createdAtOffset, OffsetDateTime lastModified, Location location) {
        this.animals = animals;
        this.albums = albums;
        this.content = content;
        this.description = description;
        this.createdAt = createdAt;
        this.createdAtOffset = createdAtOffset;
        this.lastModified = lastModified;
        this.location = location;
    }

    public UUID getId() {
        return id;
    }

    public Set<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(Set<Animal> animals) {
        this.animals = animals;
    }

    public void removeAnimal(Animal animal) {
        animals.remove(animal);
    }

    public Set<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }

    public void removeAlbum(Album album) {
        albums.remove(album);
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt.toInstant().atOffset(ZoneOffset.ofTotalSeconds(createdAtOffset));
    }

    @JsonIgnore
    public OffsetDateTime getOriginalCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public void updateLastModified() {
        lastModified = OffsetDateTime.now(Clock.systemUTC());
        LOGGER.debug("Last modified at {}", lastModified);
    }

    public Location getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Builder {
        private Set<Animal> animals = null;
        private Set<Album> albums = null;
        private byte[] content = null;
        private String description = null;
        private Location location = null;
        private OffsetDateTime createdAt = null;
        private Integer createdAtOffset = null;

        public Builder setAnimals(Set<Animal> animals) {
            this.animals = animals;
            return this;
        }

        public Builder setAlbums(Set<Album> albums) {
            this.albums = albums;
            return this;
        }

        public Builder setContent(byte[] content) {
            this.content = content;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setLocation(Location location) {
            this.location = location;
            return this;
        }

        public Builder setCreatedAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            this.createdAtOffset = createdAt.getOffset().getTotalSeconds();
            return this;
        }

        public Photo build() {
            return new Photo(animals, albums, content, description, createdAt, createdAtOffset,
                    OffsetDateTime.now(Clock.systemUTC()), location);
        }
    }
}
