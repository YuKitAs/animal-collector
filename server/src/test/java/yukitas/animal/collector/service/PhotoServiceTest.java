package yukitas.animal.collector.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import yukitas.animal.collector.model.Photo;
import yukitas.animal.collector.repository.AlbumRepository;
import yukitas.animal.collector.repository.AnimalRepository;
import yukitas.animal.collector.repository.PhotoRepository;
import yukitas.animal.collector.service.exception.EntityNotFoundException;
import yukitas.animal.collector.service.exception.InvalidDataException;
import yukitas.animal.collector.service.exception.RequiredDataNotProvidedException;
import yukitas.animal.collector.service.impl.PhotoServiceImpl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class PhotoServiceTest extends AbstractServiceTest {
    private static final UUID PHOTO_ID = UUID.randomUUID();

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
        when(photoRepository.findById(PHOTO_ID)).thenReturn(Optional.of(
                new Photo.Builder().setAnimals(Collections.emptySet()).setAlbums(Collections.emptySet()).build()));
    }

    @Test
    public void getPhotosByAlbum_AlbumNotFound() {
        assertThatThrownBy(() -> photoService.getPhotosByAlbum(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ALBUM_NOT_FOUND_MESSAGE);
    }

    @Test
    public void getPhotosByAnimal_AnimalNotFound() {
        assertThatThrownBy(() -> photoService.getPhotosByAnimal(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ANIMAL_NOT_FOUND_MESSAGE);
    }

    @Test
    public void getPhoto_PhotoNotFound() {
        assertThatThrownBy(() -> photoService.getPhoto(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(PHOTO_NOT_FOUND_MESSAGE);
    }

    @Test
    public void updatePhoto_PhotoNotFound() {
        assertThatThrownBy(() -> photoService.updatePhoto(INVALID_ID, null, null, DESCRIPTION)).isInstanceOf(
                EntityNotFoundException.class).hasMessage(PHOTO_NOT_FOUND_MESSAGE);
    }

    @Test
    public void updatePhoto_AnimalIdsNotProvided() {
        assertThatThrownBy(() -> photoService.updatePhoto(PHOTO_ID, Collections.emptySet(), Set.of(UUID.randomUUID()),
                DESCRIPTION)).isInstanceOf(RequiredDataNotProvidedException.class)
                .hasMessage(ANIMAL_IDS_NOT_PROVIDED_MESSAGE);
    }

    @Test
    public void updatePhoto_InvalidAnimalIdProvided() {
        assertThatThrownBy(() -> photoService.updatePhoto(PHOTO_ID, Set.of(INVALID_ID), Collections.emptySet(),
                DESCRIPTION)).isInstanceOf(InvalidDataException.class);
    }

    @Test
    public void updatePhoto_InvalidAlbumIdProvided() {
        assertThatThrownBy(() -> photoService.updatePhoto(PHOTO_ID, Set.of(UUID.randomUUID()), Set.of(INVALID_ID),
                DESCRIPTION)).isInstanceOf(InvalidDataException.class);
    }

    @Test
    public void deletePhoto_PhotoNotFound() {
        assertThatThrownBy(() -> photoService.deletePhoto(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(PHOTO_NOT_FOUND_MESSAGE);
    }
}