package yukitas.animal.collector.service;

import java.util.List;
import java.util.UUID;

import yukitas.animal.collector.model.Category;

public interface CategoryService {
    String ENTITY_NAME = "category";

    List<Category> getAllCategories();

    Category getCategory(UUID id);

    Category createCategory(String name);

    Category updateCategory(UUID id, String name);

    void deleteCategory(UUID id);
}
