package yukitas.animal.collector.service;

import java.util.List;
import java.util.UUID;

import yukitas.animal.collector.model.Category;

public interface CategoryService {
    List<Category> getAllCategories();

    Category getCategory(UUID id);

    Category createCategory(Category category);

    void deleteCategory(UUID id);
}
