package yukitas.animal.collector.controller;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Base64;

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
    public void updatePhoto() {
    }

    @Test
    public void deletePhoto() {
        ResponseEntity<Void> response = getTestRestTemplate().exchange("/photos/" + PHOTO_DOG_ID, HttpMethod.DELETE,
                null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}