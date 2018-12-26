package yukitas.animal.collector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "photo_animal", joinColumns = {@JoinColumn(name = "photo_id")}, inverseJoinColumns =
            {@JoinColumn(name = "animal_id")})
    @JsonIgnore
    private Set<Animal> animals = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "photo_album", joinColumns = {@JoinColumn(name = "photo_id")}, inverseJoinColumns =
            {@JoinColumn(name = "album_id")})
    @JsonIgnore
    private Set<Album> albums = new HashSet<>();

    private byte[] content;

    @Size(max = 250)
    private String description;

    private Instant createdAt;

    @Embedded
    private Location location;

    public Photo() {
    }

    private Photo(Set<Animal> animals, Set<Album> albums, byte[] content, String description, Instant createdAt,
            Location location) {
        this.animals = animals;
        this.albums = albums;
        this.content = content;
        this.description = description;
        this.createdAt = createdAt;
        this.location = location;
    }

    public UUID getId() {
        return id;
    }

    public Set<Animal> getAnimals() {
        return animals;
    }

    public Set<Album> getAlbums() {
        return albums;
    }

    public byte[] getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Location getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public static class Builder {
        private Set<Animal> animals = null;
        private Set<Album> albums = null;
        private byte[] content = null;
        private String description = null;
        private Location location = null;

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

        public Photo build() {
            return new Photo(animals, albums, content, description, Instant.now(), location);
        }
    }
}
