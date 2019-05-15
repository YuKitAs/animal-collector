package yukitas.animal.collector.model;

import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Size(max = 64)
    @Column(unique = true)
    private String name;

    private Category() {
    }

    private Category(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Builder {
        private String name = null;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Category build() {
            return new Category(name);
        }
    }
}
