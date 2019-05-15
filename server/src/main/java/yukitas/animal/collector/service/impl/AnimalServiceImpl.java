package yukitas.animal.collector.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import yukitas.animal.collector.model.Animal;
import yukitas.animal.collector.model.Category;
import yukitas.animal.collector.repository.AnimalRepository;
import yukitas.animal.collector.repository.CategoryRepository;
import yukitas.animal.collector.repository.PhotoRepository;
import yukitas.animal.collector.service.AnimalService;
import yukitas.animal.collector.service.PhotoService;
import yukitas.animal.collector.service.exception.EntityNotFoundException;

@Service
public class AnimalServiceImpl implements AnimalService {
    private static final Logger LOGGER = LogManager.getLogger(AnimalServiceImpl.class);
    private static final String ENTITY_NAME = "animal";

    private final CategoryRepository categoryRepository;
    private final PhotoService photoService;
    private final AnimalRepository animalRepository;
    private final PhotoRepository photoRepository;

    @Autowired
    public AnimalServiceImpl(CategoryRepository categoryRepository, PhotoService photoService,
            AnimalRepository animalRepository, PhotoRepository photoRepository) {
        this.categoryRepository = categoryRepository;
        this.photoService = photoService;
        this.animalRepository = animalRepository;
        this.photoRepository = photoRepository;
    }

    @Override
    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    @Override
    public List<Animal> getAnimalsByCategory(UUID categoryId) {
        // Only to check if categoryId exists
        findCategoryById(categoryId);

        return animalRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Animal> getAnimalsByPhoto(UUID photoId) {
        return animalRepository.findAll()
                .stream()
                .filter(animal -> animal.getPhotos().contains(photoService.getPhoto(photoId)))
                .collect(Collectors.toList());
    }

    @Override
    public Animal getAnimal(UUID id) {
        return findAnimalById(id);
    }

    @Override
    public Animal createAnimal(UUID categoryId, String name, String[] tags) {
        LOGGER.trace("Creating animal with [name='{}', tags={}] for category '{}'", name, tags, categoryId);
        return animalRepository.save(
                new Animal.Builder().setCategory(findCategoryById(categoryId)).setName(name).setTags(tags).build());
    }

    @Override
    public Animal updateAnimal(UUID id, String name, String[] tags) {
        LOGGER.trace("Updating animal '{}' with [name='{}', tags={}]", id, name, tags);

        Animal animal = findAnimalById(id);

        if (name != null && !name.isBlank()) {
            animal.setName(name);
        }

        animal.setTags(Objects.requireNonNullElseGet(tags, () -> new String[0]));

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

        LOGGER.debug("Deleting animal [id={}, name='{}']", id, animal.getName());
        animalRepository.deleteById(id);
    }

    private Animal findAnimalById(UUID id) {
        return animalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
    }

    private Category findCategoryById(UUID id) {
        // Not using categoryService#findCategoryById mainly because it would cause circular dependencies
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("category", id));
    }
}
