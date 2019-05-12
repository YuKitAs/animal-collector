package yukitas.animal.collector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import yukitas.animal.collector.controller.dto.CreateCategoryRequest;
import yukitas.animal.collector.model.Category;
import yukitas.animal.collector.service.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        return new ResponseEntity<>(categoryService.createCategory(
                CreateCategoryRequest.builder().setName(createCategoryRequest.getName()).build()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable("id") UUID categoryId,
            @Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        return new ResponseEntity<>(categoryService.updateCategory(categoryId, createCategoryRequest.getName()),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("id") UUID categoryId) {
        categoryService.deleteCategory(categoryId);
    }
}
