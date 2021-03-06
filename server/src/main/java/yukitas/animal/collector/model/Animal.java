package yukitas.animal.collector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "animals")
public class Animal {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category;

    @Size(max = 32)
    private String name;

    private String[] tags;

    @ManyToMany(mappedBy = "animals")
    @OrderBy("last_modified DESC")
    @JsonIgnore
    private Set<Photo> photos = new HashSet<>();

    private Animal() {
    }

    private Animal(Category category, String name, String[] tags) {
        this.category = category;
        this.name = name;
        this.tags = tags;
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

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Set<Photo> getPhotos() {
        return photos;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public static class Builder {
        private Category category;
        private String name;
        private String[] tags;

        public Builder setCategory(Category category) {
            this.category = category;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setTags(String[] tags) {
            this.tags = Objects.requireNonNullElseGet(tags, () -> new String[0]);
            return this;
        }

        public Animal build() {
            return new Animal(category, name, tags);
        }
    }
}
