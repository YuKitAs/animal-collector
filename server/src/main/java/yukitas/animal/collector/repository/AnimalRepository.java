package yukitas.animal.collector.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import yukitas.animal.collector.model.Animal;

@Repository
public interface AnimalRepository extends CrudRepository<Animal, UUID> {
    List<Animal> findAll();

    List<Animal> findByIdIn(Set<UUID> animalIds);

    List<Animal> findByCategoryId(UUID categoryId);
}
