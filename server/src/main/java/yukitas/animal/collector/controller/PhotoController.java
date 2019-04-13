package yukitas.animal.collector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import yukitas.animal.collector.controller.dto.CreatePhotoRequest;
import yukitas.animal.collector.controller.dto.CreatePhotoResponse;
import yukitas.animal.collector.controller.dto.UpdatePhotoRequest;
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
    public ResponseEntity<CreatePhotoResponse> createPhoto(
            @Valid @RequestParam("content") MultipartFile content) throws IOException {
        return new ResponseEntity<>(
                new CreatePhotoResponse(photoService.createPhoto(CreatePhotoRequest.builder(), content.getBytes())),
                HttpStatus.CREATED);
    }

    @GetMapping("/albums/{album_id}/photos")
    public ResponseEntity<List<Photo>> getPhotosByAlbum(@PathVariable("album_id") UUID albumId) {
        return new ResponseEntity<>(photoService.getPhotosByAlbum(albumId), HttpStatus.OK);
    }

    @GetMapping("/albums/{album_id}/photos/latest")
    public ResponseEntity<Photo> getLatestPhotoByAlbum(@PathVariable("album_id") UUID albumId) {
        return photoService.getLatestPhotoByAlbum(albumId)
                .map(photo -> new ResponseEntity<>(photo, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping("/animals/{animal_id}/photos")
    public ResponseEntity<List<Photo>> getPhotosByAnimal(@PathVariable("animal_id") UUID animalId) {
        return new ResponseEntity<>(photoService.getPhotosByAnimal(animalId), HttpStatus.OK);
    }

    @GetMapping("/animals/{animal_id}/photos/latest")
    public ResponseEntity<Photo> getLatestPhotoByAnimal(@PathVariable("animal_id") UUID animalId) {
        return photoService.getLatestPhotoByAnimal(animalId)
                .map(photo -> new ResponseEntity<>(photo, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping("/photos/{id}")
    public ResponseEntity<Photo> getPhoto(@PathVariable("id") UUID photoId) {
        return new ResponseEntity<>(photoService.getPhoto(photoId), HttpStatus.OK);
    }

    @PutMapping("/photos/{id}")
    public ResponseEntity<Void> updatePhoto(@PathVariable("id") UUID photoId,
            @RequestBody UpdatePhotoRequest updatePhotoRequest) {
        photoService.updatePhoto(photoId, updatePhotoRequest.getAnimalIds(), updatePhotoRequest.getAlbumIds(),
                updatePhotoRequest.getDescription());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/photos/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable("id") UUID photoId) {
        photoService.deletePhoto(photoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
