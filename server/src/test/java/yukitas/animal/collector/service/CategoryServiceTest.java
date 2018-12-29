package yukitas.animal.collector.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import yukitas.animal.collector.repository.CategoryRepository;
import yukitas.animal.collector.service.exception.EntityNotFoundException;
import yukitas.animal.collector.service.impl.CategoryServiceImpl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class CategoryServiceTest extends AbstractServiceTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Before
    public void setUp() {
        when(categoryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
    }

    @Test
    public void getCategoryWithInvalidId() {
        assertThatThrownBy(() -> categoryService.getCategory(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(CATEGORY_NOT_FOUND_MESSAGE);
    }

    @Test
    public void updateCategory() {
        assertThatThrownBy(() -> categoryService.updateCategory(INVALID_ID, "new-name")).isInstanceOf(
                EntityNotFoundException.class).hasMessage(CATEGORY_NOT_FOUND_MESSAGE);
    }

    @Test
    public void deleteCategoryWithInvalidId() {
        assertThatThrownBy(() -> categoryService.deleteCategory(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(CATEGORY_NOT_FOUND_MESSAGE);
    }
}