package yukitas.animal.collector.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import yukitas.animal.collector.repository.AlbumRepository;
import yukitas.animal.collector.repository.CategoryRepository;
import yukitas.animal.collector.service.exception.EntityNotFoundException;
import yukitas.animal.collector.service.impl.AlbumServiceImpl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class AlbumServiceImplTest extends AbstractServiceTest {
    @InjectMocks
    private AlbumServiceImpl albumService;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Before
    public void setUp() {
        when(categoryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        when(albumRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
    }

    @Test
    public void getAlbumsByCategory() {
        assertThatThrownBy(() -> albumService.getAlbumsByCategory(INVALID_ID)).isInstanceOf(
                EntityNotFoundException.class).hasMessage(CATEGORY_NOT_FOUND_MESSAGE);
    }

    @Test
    public void getAlbum() {
        assertThatThrownBy(() -> albumService.getAlbum(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ALBUM_NOT_FOUND_MESSAGE);
    }

    @Test
    public void updateAlbum() {
        assertThatThrownBy(() -> albumService.getAlbum(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ALBUM_NOT_FOUND_MESSAGE);
    }

    @Test
    public void deleteAlbum() {
        assertThatThrownBy(() -> albumService.getAlbum(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ALBUM_NOT_FOUND_MESSAGE);
    }
}