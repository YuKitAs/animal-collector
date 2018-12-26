package yukitas.animal.collector.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import yukitas.animal.collector.model.Animal;
import yukitas.animal.collector.repository.AnimalRepository;
import yukitas.animal.collector.service.AnimalService;
import yukitas.animal.collector.service.exception.EntityNotFoundException;

@Service
public class AnimalServiceImpl implements AnimalService {
    private final AnimalRepository animalRepository;

    @Autowired
    public AnimalServiceImpl(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Override
    public List<Animal> getAllAnimals() {
        return StreamSupport.stream(animalRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public List<Animal> getAnimalsByCategory(UUID categoryId) {
        return getAllAnimals().stream()
                .filter(animal -> animal.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }

    @Override
    public Animal getAnimal(UUID id) {
        return animalRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Animal not found by id=%s", id.toString())));
    }

    @Override
    public Animal createAnimal(Animal animal) {
        return animalRepository.save(animal);
    }
}
