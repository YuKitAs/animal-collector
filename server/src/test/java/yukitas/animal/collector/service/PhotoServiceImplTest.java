package yukitas.animal.collector.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import yukitas.animal.collector.repository.AlbumRepository;
import yukitas.animal.collector.repository.AnimalRepository;
import yukitas.animal.collector.repository.PhotoRepository;
import yukitas.animal.collector.service.exception.EntityNotFoundException;
import yukitas.animal.collector.service.impl.PhotoServiceImpl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class PhotoServiceImplTest extends AbstractServiceTest {
    @InjectMocks
    private PhotoServiceImpl photoService;

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Before
    public void setUp() {
        when(albumRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        when(animalRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        when(photoRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
    }

    @Test
    public void getPhotosByAlbum() {
        assertThatThrownBy(() -> photoService.getPhotosByAlbum(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ALBUM_NOT_FOUND_MESSAGE);
    }

    @Test
    public void getPhotosByAnimal() {
        assertThatThrownBy(() -> photoService.getPhotosByAnimal(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ANIMAL_NOT_FOUND_MESSAGE);
    }

    @Test
    public void getPhoto() {
        assertThatThrownBy(() -> photoService.getPhoto(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(PHOTO_NOT_FOUND_MESSAGE);
    }

    @Test
    public void updatePhoto() {
        assertThatThrownBy(() -> photoService.updatePhoto(INVALID_ID, null, null, "new-description")).isInstanceOf(
                EntityNotFoundException.class).hasMessage(PHOTO_NOT_FOUND_MESSAGE);
    }

    @Test
    public void deletePhoto() {
        assertThatThrownBy(() -> photoService.deletePhoto(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(PHOTO_NOT_FOUND_MESSAGE);
    }
}