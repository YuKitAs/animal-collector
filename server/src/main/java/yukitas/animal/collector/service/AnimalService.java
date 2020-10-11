package yukitas.animal.collector.service;

import java.util.List;
import java.util.UUID;

import yukitas.animal.collector.model.Animal;

public interface AnimalService {
    String ENTITY_NAME = "animal";

    List<Animal> getAllAnimals();

    List<Animal> getAnimalsByCategory(UUID categoryId);

    List<Animal> getAnimalsByPhoto(UUID photoId);

    Animal getAnimal(UUID id);

    Animal createAnimal(UUID categoryId, String name, String[] tags);

    Animal updateAnimal(UUID id, String name, String[] tags);

    void deleteAnimal(UUID id);
}
