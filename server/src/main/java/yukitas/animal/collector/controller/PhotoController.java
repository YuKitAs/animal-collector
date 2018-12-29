package yukitas.animal.collector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import yukitas.animal.collector.controller.dto.CreatePhotoRequest;
import yukitas.animal.collector.model.Photo;
import yukitas.animal.collector.service.PhotoService;

@RestController
public class PhotoController {
    private final PhotoService photoService;

    @Autowired
    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping("/photos")
    public ResponseEntity<Photo> createPhoto(@Valid @RequestBody CreatePhotoRequest createPhotoRequest) {
        return new ResponseEntity<>(
                photoService.createPhoto(CreatePhotoRequest.builder(), createPhotoRequest.getAnimalIds(),
                        createPhotoRequest.getAlbumIds(), createPhotoRequest.getContent(),
                        createPhotoRequest.getDescription(), createPhotoRequest.getLocation()), HttpStatus.CREATED);
    }

    @GetMapping("/albums/{album_id}/photos")
    public ResponseEntity<List<Photo>> getPhotosByAlbum(@PathVariable("album_id") UUID albumId) {
        return new ResponseEntity<>(photoService.getPhotosByAlbum(albumId), HttpStatus.OK);
    }

    @GetMapping("/animals/{animal_id}/photos")
    public ResponseEntity<List<Photo>> getPhotosByAnimal(@PathVariable("animal_id") UUID animalId) {
        return new ResponseEntity<>(photoService.getPhotosByAnimal(animalId), HttpStatus.OK);
    }

    @GetMapping("/photos/{id}")
    public ResponseEntity<Photo> getPhoto(@PathVariable("id") UUID photoId) {
        return new ResponseEntity<>(photoService.getPhoto(photoId), HttpStatus.OK);
    }

    @PutMapping("/photos/{id}")
    public ResponseEntity<Photo> updatePhoto(@PathVariable("id") UUID photoId,
            @RequestBody CreatePhotoRequest createPhotoRequest) {
        return new ResponseEntity<>(
                photoService.updatePhoto(photoId, createPhotoRequest.getAnimalIds(), createPhotoRequest.getAlbumIds(),
                        createPhotoRequest.getDescription()), HttpStatus.OK);
    }

    @DeleteMapping("/photos/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable("id") UUID photoId) {
        photoService.deletePhoto(photoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
