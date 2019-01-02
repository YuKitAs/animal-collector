package yukitas.animal.collector.controller;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Base64;
import java.util.Objects;

import yukitas.animal.collector.controller.dto.CreatePhotoRequest;
import yukitas.animal.collector.model.Photo;

import static org.assertj.core.api.Assertions.assertThat;

public class PhotoControllerTest extends AbstractControllerTest {

    @Test
    public void createPhoto() throws Exception {
        ResponseEntity<Photo> response = getTestRestTemplate().postForEntity("/photos",
                getFixture("create-photo.json", CreatePhotoRequest.class), Photo.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).satisfies(photo -> {
            assertThat(photo.getId()).isNotNull();
            assertThat(Base64.getEncoder().encode(photo.getContent())).isEqualTo(PHOTO_CAT_1_DOG_CONTENT);
            assertThat(photo.getDescription()).isEqualTo(PHOTO_CAT_1_DOG_DESCRIPTION);
        });

        photoAddedForAlbumCat1();
        photoAddedForAlbumDog();
        photoAddedForCat1();
        photoAddedForDog();
    }

    private void photoAddedForAlbumCat1() {
        ResponseEntity<Photo[]> albumCat1PhotosResponse = getTestRestTemplate().getForEntity(
                String.format("/albums/%s/photos", ALBUM_CAT_1_ID), Photo[].class);

        assertThat(albumCat1PhotosResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(albumCat1PhotosResponse.getBody()).length).isEqualTo(2);
    }

    private void photoAddedForAlbumDog() {
        ResponseEntity<Photo[]> albumDogPhotosResponse = getTestRestTemplate().getForEntity(
                String.format("/albums/%s/photos", ALBUM_DOG_ID), Photo[].class);

        assertThat(albumDogPhotosResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(albumDogPhotosResponse.getBody()).length).isEqualTo(2);
    }

    private void photoAddedForCat1() {
        ResponseEntity<Photo[]> animalCat1PhotosResponse = getTestRestTemplate().getForEntity(
                String.format("/animals/%s/photos", ANIMAL_CAT_1_ID), Photo[].class);

        assertThat(animalCat1PhotosResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(animalCat1PhotosResponse.getBody()).length).isEqualTo(2);
    }

    private void photoAddedForDog() {
        ResponseEntity<Photo[]> animalDogPhotosResponse = getTestRestTemplate().getForEntity(
                String.format("/animals/%s/photos", ANIMAL_DOG_ID), Photo[].class);

        assertThat(animalDogPhotosResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(animalDogPhotosResponse.getBody()).length).isEqualTo(2);
    }

    @Test
    public void getPhotosByAlbum() {
        ResponseEntity<Photo[]> response = getTestRestTemplate().getForEntity(
                String.format("/albums/%s/photos", ALBUM_CAT_1_ID), Photo[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(photos -> {
            assertThat(photos.length).isEqualTo(1);

            Photo photo = photos[0];
            assertThat(photo.getId()).isEqualTo(PHOTO_CAT_1_ID);
            assertThat(photo.getContent()).isEqualTo(PHOTO_CAT_1_CONTENT);
            assertThat(photo.getDescription()).isEqualTo(PHOTO_CAT_1_DESCRIPTION);
        });
    }

    @Test
    public void getPhotosByAnimal() {
        ResponseEntity<Photo[]> response = getTestRestTemplate().getForEntity(
                String.format("/animals/%s/photos", ANIMAL_DOG_ID), Photo[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(photos -> {
            assertThat(photos.length).isEqualTo(1);

            Photo photo = photos[0];
            assertThat(photo.getId()).isEqualTo(PHOTO_DOG_ID);
            assertThat(photo.getContent()).isEqualTo(PHOTO_DOG_CONTENT);
            assertThat(photo.getDescription()).isEqualTo(PHOTO_DOG_DESCRIPTION);
        });
    }

    @Test
    public void getPhotoById() {
        ResponseEntity<Photo> response = getTestRestTemplate().getForEntity("/photos/" + PHOTO_CAT_1_ID, Photo.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(
                photo -> assertThat(photo.getContent()).isEqualTo(PHOTO_CAT_1_CONTENT));
    }

    @Test
    public void updatePhoto_ContentUnchanged() throws Exception {
        ResponseEntity<Photo> response = getTestRestTemplate().exchange("/photos/" + PHOTO_CAT_1_ID, HttpMethod.PUT,
                new HttpEntity<>(getFixture("create-photo.json", CreatePhotoRequest.class)), Photo.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert response.getBody() != null;
        assertThat(response.getBody()).satisfies(photo -> {
            assertThat(photo.getId()).isEqualTo(PHOTO_CAT_1_ID);
            assertThat(photo.getDescription()).isEqualTo(PHOTO_CAT_1_DOG_DESCRIPTION);
            assertThat(photo.getContent()).isEqualTo(PHOTO_CAT_1_CONTENT);
        });

        photoAddedForDog();
    }

    @Test
    public void deletePhoto() {
        ResponseEntity<Void> response = getTestRestTemplate().exchange("/photos/" + PHOTO_DOG_ID, HttpMethod.DELETE,
                null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}