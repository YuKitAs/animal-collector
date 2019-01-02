package yukitas.animal.collector.controller;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import yukitas.animal.collector.controller.dto.CreateAlbumRequest;
import yukitas.animal.collector.model.Album;

import static org.assertj.core.api.Assertions.assertThat;

public class AlbumControllerTest extends AbstractControllerTest {

    @Test
    public void getAlbumsByCategory() {
        ResponseEntity<Album[]> response = getTestRestTemplate().getForEntity(
                String.format("/categories/%s/albums", CATEGORY_CAT_ID), Album[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(albums -> {
            assertThat(albums.length).isEqualTo(1);
            isAlbumCat1(albums[0]);
        });
    }

    @Test
    public void getAlbumById() {
        ResponseEntity<Album> response = getTestRestTemplate().getForEntity("/albums/" + ALBUM_DOG_ID, Album.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(this::isAlbumDog);
    }

    private void isAlbumCat1(Album album) {
        assertThat(album.getId()).isEqualTo(ALBUM_CAT_1_ID);
        assertThat(album.getName()).isEqualTo(ALBUM_CAT_1_NAME);
        assert album.getPhotos().stream().findAny().isPresent();
        assertThat(album.getPhotos().stream().findAny().get().getId()).isEqualTo((PHOTO_CAT_1_ID));
    }

    private void isAlbumDog(Album album) {
        assertThat(album.getId()).isEqualTo(ALBUM_DOG_ID);
        assertThat(album.getName()).isEqualTo(ALBUM_DOG_NAME);
        assert album.getPhotos().stream().findAny().isPresent();
        assertThat(album.getPhotos().stream().findAny().get().getId()).isEqualTo((PHOTO_DOG_ID));
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