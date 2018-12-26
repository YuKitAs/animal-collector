package yukitas.animal.collector.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import yukitas.animal.collector.model.Animal;

@Repository
public interface AnimalRepository extends CrudRepository<Animal, UUID> {
}
