package yukitas.animal.collector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import yukitas.animal.collector.controller.dto.CreateAnimalRequest;
import yukitas.animal.collector.controller.dto.UpdateAnimalRequest;
import yukitas.animal.collector.model.Animal;
import yukitas.animal.collector.service.AnimalService;

@RestController
public class AnimalController {
    private final AnimalService animalService;

    @Autowired
    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping("/animals")
    public ResponseEntity<List<Animal>> getAllAnimals() {
        return new ResponseEntity<>(animalService.getAllAnimals(), HttpStatus.OK);
    }

    @GetMapping("/categories/{cat_id}/animals")
    public ResponseEntity<List<Animal>> getAnimalsByCategory(@PathVariable("cat_id") UUID categoryId) {
        return new ResponseEntity<>(animalService.getAnimalsByCategory(categoryId), HttpStatus.OK);
    }

    @GetMapping("/photos/{photo_id}/animals")
    public ResponseEntity<List<Animal>> getAnimalsByPhoto(@PathVariable("photo_id") UUID photoId) {
        return new ResponseEntity<>(animalService.getAnimalsByPhoto(photoId), HttpStatus.OK);
    }

    @GetMapping("/animals/{id}")
    public ResponseEntity<Animal> getAnimal(@PathVariable("id") UUID animalId) {
        return new ResponseEntity<>(animalService.getAnimal(animalId), HttpStatus.OK);
    }

    @PostMapping("/categories/{cat_id}/animals")
    public ResponseEntity<Animal> createAnimal(@PathVariable("cat_id") UUID categoryId,
            @Valid @RequestBody CreateAnimalRequest createAnimalRequest) {
        return new ResponseEntity<>(
                animalService.createAnimal(categoryId, createAnimalRequest.getName(), createAnimalRequest.getTags()),
                HttpStatus.CREATED);
    }

    @PutMapping("/animals/{id}")
    public ResponseEntity<Animal> updateAnimal(@PathVariable("id") UUID animalId,
            @Valid @RequestBody UpdateAnimalRequest updateAnimalRequest) {
        return new ResponseEntity<>(
                animalService.updateAnimal(animalId, updateAnimalRequest.getName(), updateAnimalRequest.getTags()),
                HttpStatus.OK);
    }

    @DeleteMapping("/animals/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAnimal(@PathVariable("id") UUID animalId) {
        animalService.deleteAnimal(animalId);
    }
}
