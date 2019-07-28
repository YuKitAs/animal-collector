package yukitas.animal.collector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import yukitas.animal.collector.configuration.JacksonConfig;
import yukitas.animal.collector.controller.dto.CreatePhotoRequest;
import yukitas.animal.collector.controller.dto.CreatePhotoResponse;
import yukitas.animal.collector.controller.dto.UpdatePhotoRequest;
import yukitas.animal.collector.model.Photo;
import yukitas.animal.collector.service.PhotoService;

@RestController
public class PhotoController {
    private final PhotoService photoService;
    private final JacksonConfig jacksonConfig;

    @Autowired
    public PhotoController(PhotoService photoService, JacksonConfig jacksonConfig) {
        this.photoService = photoService;
        this.jacksonConfig = jacksonConfig;
    }

    @PostMapping("/photos")
    @ResponseStatus(HttpStatus.CREATED)
    public CreatePhotoResponse createPhoto(@Valid @RequestParam("content") MultipartFile content,
            @RequestParam("created_at") String createdAt) throws IOException {
        return new CreatePhotoResponse(photoService.createPhoto(CreatePhotoRequest.builder(), content.getBytes(),
                jacksonConfig.objectMapper().readValue(createdAt, OffsetDateTime.class)));
    }

    @GetMapping("/albums/{album_id}/photos")
    public List<Photo> getPhotosByAlbum(@PathVariable("album_id") UUID albumId,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height) {
        return (width != null && height != null) ? photoService.getPhotosByAlbum(albumId, width,
                height) : photoService.getPhotosByAlbum(albumId);
    }

    @GetMapping("/albums/{album_id}/photos/latest")
    public ResponseEntity<Photo> getLatestPhotoByAlbum(@PathVariable("album_id") UUID albumId,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height) {
        Optional<Photo> photoOptional = (width != null && height != null) ? photoService.getLatestPhotoByAlbum(albumId,
                width, height) : photoService.getLatestPhotoByAlbum(albumId);
        return photoOptional.map(photo -> new ResponseEntity<>(photo, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping("/animals/{animal_id}/photos")
    public List<Photo> getPhotosByAnimal(@PathVariable("animal_id") UUID animalId,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height) {
        return (width != null && height != null) ? photoService.getPhotosByAnimal(animalId, width,
                height) : photoService.getPhotosByAnimal(animalId);
    }

    @GetMapping("/animals/{animal_id}/photos/latest")
    public ResponseEntity<Photo> getLatestPhotoByAnimal(@PathVariable("animal_id") UUID animalId,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height) {
        Optional<Photo> photoOptional = (width != null && height != null) ? photoService.getLatestPhotoByAnimal(
                animalId, width, height) : photoService.getLatestPhotoByAnimal(animalId);
        return photoOptional.map(photo -> new ResponseEntity<>(photo, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping("/photos/{id}")
    public Photo getPhoto(@PathVariable("id") UUID photoId) {
        return photoService.getPhoto(photoId);
    }

    @PutMapping("/photos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePhoto(@PathVariable("id") UUID photoId,
            @Valid @RequestBody UpdatePhotoRequest updatePhotoRequest) {
        photoService.updatePhoto(photoId, updatePhotoRequest.getAnimalIds(), updatePhotoRequest.getAlbumIds(),
                updatePhotoRequest.getDescription());
    }

    @DeleteMapping("/photos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhoto(@PathVariable("id") UUID photoId) {
        photoService.deletePhoto(photoId);
    }
}
