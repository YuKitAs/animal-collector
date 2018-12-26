package yukitas.animal.collector.service;

import java.util.List;
import java.util.UUID;

import yukitas.animal.collector.model.Animal;

public interface AnimalService {
    List<Animal> getAllAnimals();

    List<Animal> getAnimalsByCategory(UUID categoryId);

    Animal getAnimal(UUID id);

    Animal createAnimal(Animal animal);
}
