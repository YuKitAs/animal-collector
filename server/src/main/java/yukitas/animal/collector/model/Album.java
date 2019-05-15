package yukitas.animal.collector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "albums")
public class Album {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Category category;

    @Size(max = 64)
    private String name;

    @ManyToMany(mappedBy = "albums")
    @OrderBy("created_at DESC")
    @JsonIgnore
    private Set<Photo> photos = new HashSet<>();

    private Album() {
    }

    private Album(Category category, String name) {
        this.category = category;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Photo> getPhotos() {
        return photos;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public static class Builder {
        private Category category = null;
        private String name = null;

        public Album.Builder setCategory(Category category) {
            this.category = category;
            return this;
        }

        public Album.Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Album build() {
            return new Album(category, name);
        }
    }
}
