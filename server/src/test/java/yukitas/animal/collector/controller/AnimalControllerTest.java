package yukitas.animal.collector.controller;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import yukitas.animal.collector.controller.dto.CreateAnimalRequest;
import yukitas.animal.collector.model.Animal;

import static org.assertj.core.api.Assertions.assertThat;

public class AnimalControllerTest extends AbstractControllerTest {

    @Test
    public void getAllAnimals() {
        ResponseEntity<Animal[]> response = getTestRestTemplate().getForEntity("/animals", Animal[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(animals -> {
            assertThat(animals.length).isEqualTo(2);
            isAnimalCat1(animals[0]);
            isAnimalDog(animals[1]);
        });
    }

    @Test
    public void getAnimalsByCategory() {
        ResponseEntity<Animal[]> response = getTestRestTemplate().getForEntity(
                String.format("/categories/%s/animals", CATEGORY_CAT_ID), Animal[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(animals -> {
            assertThat(animals.length).isEqualTo(1);
            isAnimalCat1(animals[0]);
        });
    }

    @Test
    public void getAnimalById() {
        ResponseEntity<Animal> response = getTestRestTemplate().getForEntity("/animals/" + ANIMAL_DOG_ID, Animal.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(this::isAnimalDog);
    }

    private void isAnimalCat1(Animal animal) {
        assertThat(animal.getId()).isEqualTo(ANIMAL_CAT_1_ID);
        assertThat(animal.getName()).isEqualTo(ANIMAL_CAT_1_NAME);
        assert animal.getPhotos().stream().findAny().isPresent();
        assertThat(animal.getPhotos().stream().findAny().get().getId()).isEqualTo((PHOTO_CAT_1_ID));
    }

    private void isAnimalDog(Animal animal) {
        assertThat(animal.getId()).isEqualTo(ANIMAL_DOG_ID);
        assertThat(animal.getName()).isEqualTo(ANIMAL_DOG_NAME);
        assert animal.getPhotos().stream().findAny().isPresent();
        assertThat(animal.getPhotos().stream().findAny().get().getId()).isEqualTo((PHOTO_DOG_ID));
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