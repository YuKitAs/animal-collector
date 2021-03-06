package yukitas.animal.collector.controller;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Random;

import yukitas.animal.collector.controller.dto.CreatePhotoResponse;
import yukitas.animal.collector.controller.dto.GetPhotoResponse;
import yukitas.animal.collector.controller.dto.UpdatePhotoRequest;
import yukitas.animal.collector.model.AnimalClass;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = ImageRecognitionServiceConfiguration.class)
public class PhotoControllerTest extends AbstractControllerTest {
    @Test
    public void createPhoto_WithoutRecognition() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("content", new ClassPathResource("fixtures/images/photo.jpg"));
        body.add("created_at", OffsetDateTime.now());
        body.add("latitude", -90 + 180 * new Random().nextDouble());
        body.add("longitude", -180 + 360 * new Random().nextDouble());
        body.add("address", LOCATION_ADDR);

        ResponseEntity<CreatePhotoResponse> response = getTestRestTemplate().postForEntity("/photos", new HttpEntity<>(body, headers), CreatePhotoResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(Objects.requireNonNull(response.getBody()).getId()).isNotNull();
        assertThat(response.getBody().getRecognizedCategory()).isNull();
    }

    @Test
    public void createPhoto_RecognitionEnabled() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("content", new ClassPathResource("fixtures/images/photo.jpg"));
        body.add("created_at", OffsetDateTime.now());
        body.add("recognize", true);

        ResponseEntity<CreatePhotoResponse> response = getTestRestTemplate().postForEntity("/photos",
                new HttpEntity<>(body, headers), CreatePhotoResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(Objects.requireNonNull(response.getBody()).getId()).isNotNull();
        assertThat(response.getBody().getRecognizedCategory()).isEqualTo(AnimalClass.DOG);
    }

    @Test
    public void getPhotosByAlbum() {
        ResponseEntity<GetPhotoResponse[]> response = getTestRestTemplate().getForEntity(
                String.format("/albums/%s/photos", ALBUM_CAT_1_ID), GetPhotoResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(photos -> {
            assertThat(photos.length).isEqualTo(1);

            GetPhotoResponse photo = photos[0];
            assertThat(photo.getId()).isEqualTo(PHOTO_CAT_1_ID);
            assertThat(photo.getContent()).isEqualTo(PHOTO_CAT_1_CONTENT);
            assertThat(photo.getDescription()).isEqualTo(PHOTO_CAT_1_DESCRIPTION);
            assertThat(photo.getLocation().getAddress()).isEqualTo(LOCATION_ADDR);
            assertThat(photo.getCreatedAt()).isEqualTo(
                    LocalDateTime.of(2019, 6, 1, 3, 0, 0).atOffset(ZoneOffset.ofHours(3)));
        });
    }

    @Test
    public void getPhotosByAnimal() {
        ResponseEntity<GetPhotoResponse[]> response = getTestRestTemplate().getForEntity(String.format("/animals/%s" +
                "/photos", ANIMAL_DOG_ID), GetPhotoResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(photos -> {
            assertThat(photos.length).isEqualTo(1);

            GetPhotoResponse photo = photos[0];
            assertThat(photo.getId()).isEqualTo(PHOTO_DOG_ID);
            assertThat(photo.getContent()).isEqualTo(PHOTO_DOG_CONTENT);
            assertThat(photo.getDescription()).isEqualTo(PHOTO_DOG_DESCRIPTION);
            assertThat(photo.getLocation().getAddress()).isEqualTo(LOCATION_ADDR);
            assertThat(photo.getCreatedAt()).isEqualTo(
                    LocalDateTime.of(2019, 6, 2, 5, 0, 0).atOffset(ZoneOffset.ofHours(3)));
        });
    }

    @Test
    public void getPhotoById() {
        ResponseEntity<GetPhotoResponse> response = getTestRestTemplate().getForEntity("/photos/" + PHOTO_CAT_1_ID,
                GetPhotoResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(photo -> {
            assertThat(photo.getContent()).isEqualTo(PHOTO_CAT_1_CONTENT);
            assertThat(photo.getLocation().getAddress()).isEqualTo(LOCATION_ADDR);
        });
    }

    @Test
    public void updatePhoto() throws Exception {
        ResponseEntity<Void> response = getTestRestTemplate().exchange("/photos/" + PHOTO_CAT_1_ID, HttpMethod.PUT, new HttpEntity<>(getFixture("update-photo.json", UpdatePhotoRequest.class)), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verifyPhotoAddedForAnimalDog();
        verifyPhotoAddedForAlbumDog();
    }

    private void verifyPhotoAddedForAnimalDog() {
        ResponseEntity<GetPhotoResponse[]> response = getTestRestTemplate().getForEntity(String.format(
                "/animals/%s/photos", ANIMAL_DOG_ID), GetPhotoResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).length).isEqualTo(2);
    }

    private void verifyPhotoAddedForAlbumDog() {
        ResponseEntity<GetPhotoResponse[]> response = getTestRestTemplate().getForEntity(String.format(
                "/albums/%s/photos", ALBUM_DOG_ID), GetPhotoResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).length).isEqualTo(2);
    }

    @Test
    public void deletePhoto() {
        ResponseEntity<Void> response = getTestRestTemplate().exchange("/photos/" + PHOTO_DOG_ID, HttpMethod.DELETE,
                null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}