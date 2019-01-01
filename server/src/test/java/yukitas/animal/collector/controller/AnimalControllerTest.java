package yukitas.animal.collector.controller;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import yukitas.animal.collector.controller.dto.CreateAnimalRequest;
import yukitas.animal.collector.model.Animal;
import yukitas.animal.collector.model.Photo;

import static org.assertj.core.api.Assertions.assertThat;

public class AnimalControllerTest extends AbstractControllerTest {

    @Test
    public void getAllAnimals() {
        ResponseEntity<Animal[]> response = getTestRestTemplate().getForEntity("/animals", Animal[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(animals -> {
            assertThat(animals.length).isEqualTo(2);
            assertThat(isAnimalCat1(animals[0])).isTrue();
            assertThat(isAnimalDog(animals[1])).isTrue();
        });
    }

    @Test
    public void getAnimalsByCategory() {
        ResponseEntity<Animal[]> response = getTestRestTemplate().getForEntity(
                String.format("/categories/%s/animals", CATEGORY_CAT_ID), Animal[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(animals -> {
            assertThat(animals.length).isEqualTo(1);
            assertThat(isAnimalCat1(animals[0])).isTrue();
        });
    }

    @Test
    public void getAnimalById() {
        ResponseEntity<Animal> response = getTestRestTemplate().getForEntity("/animals/" + ANIMAL_DOG_ID, Animal.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(animal -> assertThat(isAnimalDog(animal)).isTrue());
    }

    private boolean isAnimalCat1(Animal animal) {
        Optional<Photo> photo = animal.getPhotos().stream().findAny();
        assert photo.isPresent();

        return animal.getId().equals(ANIMAL_CAT_1_ID) && animal.getName().equals(ANIMAL_CAT_1_NAME) && photo.get()
                .getId()
                .equals(PHOTO_CAT_1_ID);
    }

    private boolean isAnimalDog(Animal animal) {
        Optional<Photo> photo = animal.getPhotos().stream().findAny();
        assert photo.isPresent();

        return animal.getId().equals(ANIMAL_DOG_ID) && animal.getName().equals(ANIMAL_DOG_NAME) && photo.get()
                .getId()
                .equals(PHOTO_DOG_ID);
    }

    @Test
    public void createAnimal() throws Exception {
        ResponseEntity<Animal> response = getTestRestTemplate().postForEntity(
                String.format("/categories/%s/animals", CATEGORY_CAT_ID),
                getFixture("create-animal.json", CreateAnimalRequest.class), Animal.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).satisfies(animal -> {
            assertThat(animal.getId()).isNotNull();
            assertThat(animal.getName()).isEqualTo(ANIMAL_CAT_2_NAME);
            assertThat(animal.getTags()).isEqualTo(ANIMAL_CAT_2_TAGS);
        });
    }

    @Test
    public void updateAnimal() {
    }
}