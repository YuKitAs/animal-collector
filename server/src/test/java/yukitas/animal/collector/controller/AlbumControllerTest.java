package yukitas.animal.collector.controller;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import yukitas.animal.collector.controller.dto.CreateAlbumRequest;
import yukitas.animal.collector.model.Album;

import static org.assertj.core.api.Assertions.assertThat;

public class AlbumControllerTest extends AbstractControllerTest {

    @Test
    public void getAllAlbums() {
        ResponseEntity<Album[]> response = getTestRestTemplate().getForEntity("/albums", Album[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(albums -> {
            assertThat(albums.length).isEqualTo(2);
            isAlbumCat1(albums[0]);
            isAlbumDog(albums[1]);
        });
    }

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
    public void getAlbumsByPhoto() {
        ResponseEntity<Album[]> response = getTestRestTemplate().getForEntity(
                String.format("/photos/%s/albums", PHOTO_CAT_1_ID), Album[].class);

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
        assertThat(album.getCategory().getId()).isEqualTo(CATEGORY_CAT_ID);
        assertThat(album.getName()).isEqualTo(ALBUM_CAT_1_NAME);
    }

    private void isAlbumDog(Album album) {
        assertThat(album.getId()).isEqualTo(ALBUM_DOG_ID);
        assertThat(album.getCategory().getId()).isEqualTo(CATEGORY_DOG_ID);
        assertThat(album.getName()).isEqualTo(ALBUM_DOG_NAME);
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
    public void updateAlbum() throws Exception {
        ResponseEntity<Album> response = getTestRestTemplate().exchange("/albums/" + ALBUM_CAT_1_ID, HttpMethod.PUT,
                new HttpEntity<>(getFixture("create-album.json", CreateAlbumRequest.class)), Album.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assert response.getBody() != null;
        assertThat(response.getBody()).satisfies(album -> {
            assertThat(album.getId()).isEqualTo(ALBUM_CAT_1_ID);
            assertThat(album.getName()).isEqualTo(ALBUM_CAT_2_NAME);
        });
    }

    @Test
    public void deleteAlbum() {
        ResponseEntity<Void> response = getTestRestTemplate().exchange("/albums/" + ALBUM_DOG_ID, HttpMethod.DELETE,
                null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}