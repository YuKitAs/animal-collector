package yukitas.animal.collector.controller;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import yukitas.animal.collector.controller.dto.CreateAlbumRequest;
import yukitas.animal.collector.model.Album;
import yukitas.animal.collector.model.Photo;

import static org.assertj.core.api.Assertions.assertThat;

public class AlbumControllerTest extends AbstractControllerTest {

    @Test
    public void getAlbumsByCategory() {
        ResponseEntity<Album[]> response = getTestRestTemplate().getForEntity(
                String.format("/categories/%s/albums", CATEGORY_CAT_ID), Album[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(albums -> {
            assertThat(albums.length).isEqualTo(1);
            assertThat(isAlbumCat1(albums[0])).isTrue();
        });
    }

    @Test
    public void getAlbumById() {
        ResponseEntity<Album> response = getTestRestTemplate().getForEntity("/albums/" + ALBUM_DOG_ID, Album.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(album -> assertThat(isAlbumDog(album)).isTrue());
    }

    private boolean isAlbumCat1(Album album) {
        Optional<Photo> photo = album.getPhotos().stream().findAny();
        assert photo.isPresent();

        return album.getId().equals(ALBUM_CAT_1_ID) && album.getName().equals(ALBUM_CAT_1_NAME) && photo.get()
                .getId()
                .equals(PHOTO_CAT_1_ID);
    }

    private boolean isAlbumDog(Album album) {
        Optional<Photo> photo = album.getPhotos().stream().findAny();
        assert photo.isPresent();

        return album.getId().equals(ALBUM_DOG_ID) && album.getName().equals(ALBUM_DOG_NAME) && photo.get()
                .getId()
                .equals(PHOTO_DOG_ID);
    }

    @Test
    public void createAlbum() throws Exception {
        ResponseEntity<Album> response = getTestRestTemplate().postForEntity(
                String.format("/categories/%s/albums", CATEGORY_CAT_ID),
                getFixture("create-album.json", CreateAlbumRequest.class), Album.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).satisfies(album -> {
            assertThat(album.getId()).isNotNull();
            assertThat(album.getName()).isEqualTo(ALBUM_CAT_2_NAME);
        });
    }

    @Test
    public void updateAlbum() {
    }

    @Test
    public void deleteAlbum() {
        ResponseEntity<Void> response = getTestRestTemplate().exchange("/albums/" + ALBUM_DOG_ID, HttpMethod.DELETE,
                null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}