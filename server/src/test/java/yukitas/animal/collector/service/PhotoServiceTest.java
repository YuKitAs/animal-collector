package yukitas.animal.collector.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import yukitas.animal.collector.model.Album;
import yukitas.animal.collector.model.Animal;
import yukitas.animal.collector.model.Photo;
import yukitas.animal.collector.repository.AlbumRepository;
import yukitas.animal.collector.repository.AnimalRepository;
import yukitas.animal.collector.repository.PhotoRepository;
import yukitas.animal.collector.service.exception.EntityNotFoundException;
import yukitas.animal.collector.service.exception.RequiredDataNotProvidedException;
import yukitas.animal.collector.service.impl.PhotoServiceImpl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class PhotoServiceTest extends AbstractServiceTest {
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
        Album album = new Album.Builder().build();
        UUID albumId = UUID.randomUUID();
        Photo photo = new Photo.Builder().setAnimals(Collections.emptySet()).setAlbums(Set.of(album)).build();
        UUID photoId = UUID.randomUUID();

        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        assertThatThrownBy(() -> photoService.updatePhoto(photoId, null, Set.of(albumId), DESCRIPTION)).isInstanceOf(
                RequiredDataNotProvidedException.class).hasMessage(ANIMAL_IDS_NOT_PROVIDED_MESSAGE);
    }

    @Test
    public void updatePhoto_AlbumIdsNotProvided() {
        Animal animal = new Animal.Builder().build();
        UUID animalId = UUID.randomUUID();
        Photo photo = new Photo.Builder().setAnimals(Set.of(animal)).setAlbums(Collections.emptySet()).build();
        UUID photoId = UUID.randomUUID();

        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));

        assertThatThrownBy(() -> photoService.updatePhoto(photoId, Set.of(animalId), null, DESCRIPTION)).isInstanceOf(
                RequiredDataNotProvidedException.class).hasMessage(ALBUM_IDS_NOT_PROVIDED_MESSAGE);
    }

    @Test
    public void deletePhoto_PhotoNotFound() {
        assertThatThrownBy(() -> photoService.deletePhoto(INVALID_ID)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage(PHOTO_NOT_FOUND_MESSAGE);
    }
}