package yukitas.animal.collector.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import yukitas.animal.collector.repository.AnimalRepository;
import yukitas.animal.collector.repository.CategoryRepository;
import yukitas.animal.collector.service.exception.EntityNotFoundException;
import yukitas.animal.collector.service.impl.AnimalServiceImpl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class AnimalServiceTest extends AbstractServiceTest {
    @InjectMocks
    private AnimalServiceImpl animalService;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Before
    public void setUp() {
        when(categoryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        when(animalRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
    }

    @Test
    public void getAnimalsByCategory() {
        assertThatThrownBy(() -> animalService.getAnimalsByCategory(INVALID_ID)).isInstanceOf(
                EntityNotFoundException.class).hasMessage(CATEGORY_NOT_FOUND_MESSAGE);
    }

    @Test
    public void getAnimal() {
        assertThatThrownBy(() -> animalService.getAnimal(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ANIMAL_NOT_FOUND_MESSAGE);
    }

    @Test
    public void updateAnimal() {
        assertThatThrownBy(
                () -> animalService.updateAnimal(INVALID_ID, "new-name", new String[]{"new-tag"})).isInstanceOf(
                EntityNotFoundException.class).hasMessage(ANIMAL_NOT_FOUND_MESSAGE);
    }
}