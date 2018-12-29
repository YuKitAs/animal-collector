package yukitas.animal.collector.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import yukitas.animal.collector.model.Category;
import yukitas.animal.collector.repository.CategoryRepository;
import yukitas.animal.collector.service.CategoryService;
import yukitas.animal.collector.service.exception.EntityNotFoundException;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final String ENTITY_NAME = "category";

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return StreamSupport.stream(categoryRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public Category getCategory(UUID id) {
        return findCategoryById(id);
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(UUID id, String name) {
        Category category = findCategoryById(id);

        if (name != null) {
            category.setName(name);
        }

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(UUID id) {
        findCategoryById(id);

        categoryRepository.deleteById(id);
    }

    private Category findCategoryById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
    }
}
