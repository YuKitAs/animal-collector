package yukitas.animal.collector.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import yukitas.animal.collector.model.Animal;
import yukitas.animal.collector.repository.AnimalRepository;
import yukitas.animal.collector.repository.CategoryRepository;
import yukitas.animal.collector.repository.PhotoRepository;
import yukitas.animal.collector.service.AnimalService;
import yukitas.animal.collector.service.exception.EntityNotFoundException;

@Service
public class AnimalServiceImpl implements AnimalService {
    private static final Logger LOGGER = LogManager.getLogger(AnimalServiceImpl.class);
    private static final String ENTITY_NAME = "animal";

    private final AnimalRepository animalRepository;
    private final CategoryRepository categoryRepository;
    private final PhotoRepository photoRepository;

    @Autowired
    public AnimalServiceImpl(AnimalRepository animalRepository, CategoryRepository categoryRepository,
            PhotoRepository photoRepository) {
        this.animalRepository = animalRepository;
        this.categoryRepository = categoryRepository;
        this.photoRepository = photoRepository;
    }

    @Override
    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    @Override
    public List<Animal> getAnimalsByCategory(UUID categoryId) {
        categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("category", categoryId));

        return animalRepository.findByCategoryId(categoryId);
    }

    @Override
    public Animal getAnimal(UUID id) {
        return findAnimalById(id);
    }

    @Override
    public Animal createAnimal(Animal animal) {
        return animalRepository.save(animal);
    }

    @Override
    public Animal updateAnimal(UUID id, String name, String[] tags) {
        Animal animal = findAnimalById(id);

        if (name != null) {
            animal.setName(name);
        }

        if (tags != null) {
            animal.setTags(tags);
        }

        return animalRepository.save(animal);
    }

    @Override
    public void deleteAnimal(UUID id) {
        Animal animal = findAnimalById(id);
        animal.getPhotos().forEach(photo -> {
            photo.removeAnimal(animal);
            if (photo.getAnimals().isEmpty()) {
                LOGGER.debug("Pre-remove photo (id={}) which is only associated with animal (id={})", photo.getId(),
                        id);
                photoRepository.deleteById(photo.getId());
            }
        });

        LOGGER.debug("Delete animal [id={}, name='{}']", id, animal.getName());
        animalRepository.deleteById(id);
    }

    private Animal findAnimalById(UUID id) {
        return animalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
    }
}
