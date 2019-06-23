package yukitas.animal.collector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public List<Animal> getAllAnimals() {
        return animalService.getAllAnimals();
    }

    @GetMapping("/categories/{cat_id}/animals")
    public List<Animal> getAnimalsByCategory(@PathVariable("cat_id") UUID categoryId) {
        return animalService.getAnimalsByCategory(categoryId);
    }

    @GetMapping("/photos/{photo_id}/animals")
    public List<Animal> getAnimalsByPhoto(@PathVariable("photo_id") UUID photoId) {
        return animalService.getAnimalsByPhoto(photoId);
    }

    @GetMapping("/animals/{id}")
    public Animal getAnimal(@PathVariable("id") UUID animalId) {
        return animalService.getAnimal(animalId);
    }

    @PostMapping("/categories/{cat_id}/animals")
    @ResponseStatus(HttpStatus.CREATED)
    public Animal createAnimal(@PathVariable("cat_id") UUID categoryId,
            @Valid @RequestBody CreateAnimalRequest createAnimalRequest) {
        return animalService.createAnimal(categoryId, createAnimalRequest.getName(), createAnimalRequest.getTags());
    }

    @PutMapping("/animals/{id}")
    public Animal updateAnimal(@PathVariable("id") UUID animalId,
            @Valid @RequestBody UpdateAnimalRequest updateAnimalRequest) {
        return animalService.updateAnimal(animalId, updateAnimalRequest.getName(), updateAnimalRequest.getTags());
    }

    @DeleteMapping("/animals/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAnimal(@PathVariable("id") UUID animalId) {
        animalService.deleteAnimal(animalId);
    }
}
