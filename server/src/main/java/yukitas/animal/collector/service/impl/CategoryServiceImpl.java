package yukitas.animal.collector.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import yukitas.animal.collector.model.Category;
import yukitas.animal.collector.repository.CategoryRepository;
import yukitas.animal.collector.service.AlbumService;
import yukitas.animal.collector.service.AnimalService;
import yukitas.animal.collector.service.CategoryService;
import yukitas.animal.collector.service.exception.EntityNotFoundException;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger LOGGER = LogManager.getLogger(CategoryServiceImpl.class);
    private static final String ENTITY_NAME = "category";

    private final CategoryRepository categoryRepository;
    private final AlbumService albumService;
    private final AnimalService animalService;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, AlbumService albumService,
            AnimalService animalService) {
        this.categoryRepository = categoryRepository;
        this.albumService = albumService;
        this.animalService = animalService;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategory(UUID id) {
        return findCategoryById(id);
    }

    @Override
    public Category createCategory(String name) {
        LOGGER.trace("Creating category with [name='{}']", name);
        return categoryRepository.save(new Category.Builder().setName(name).build());
    }

    @Override
    public Category updateCategory(UUID id, String name) {
        Category category = findCategoryById(id);

        if (name != null && !name.isBlank()) {
            category.setName(name);
        }

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(UUID id) {
        Category category = findCategoryById(id);

        // Have to pre-remove albums and animals manually because of a bug on cascade delete
        // Hibernate bug ticket: https://hibernate.atlassian.net/browse/HHH-12239
        albumService.getAlbumsByCategory(id).forEach(album -> albumService.deleteAlbum(album.getId()));
        animalService.getAnimalsByCategory(id).forEach(animal -> animalService.deleteAnimal(animal.getId()));

        LOGGER.debug("Deleting category [id={}, name='{}']", id, category.getName());

        categoryRepository.deleteById(id);
    }

    private Category findCategoryById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
    }
}
