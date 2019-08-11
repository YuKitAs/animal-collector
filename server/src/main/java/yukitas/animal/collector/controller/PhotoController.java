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
import java.util.stream.Collectors;

import javax.validation.Valid;

import yukitas.animal.collector.configuration.JacksonConfig;
import yukitas.animal.collector.controller.dto.CreatePhotoRequest;
import yukitas.animal.collector.controller.dto.CreatePhotoResponse;
import yukitas.animal.collector.controller.dto.GetPhotoResponse;
import yukitas.animal.collector.controller.dto.UpdatePhotoRequest;
import yukitas.animal.collector.model.Location;
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
            @RequestParam("created_at") String createdAt,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "recognize", required = false) boolean recognitionEnabled) throws IOException {
        return photoService.createPhoto(CreatePhotoRequest.builder(), content.getBytes(),
                jacksonConfig.objectMapper().readValue(createdAt, OffsetDateTime.class),
                new Location(latitude, longitude, address), recognitionEnabled);
    }

    @GetMapping("/albums/{album_id}/photos")
    public List<GetPhotoResponse> getPhotosByAlbum(@PathVariable("album_id") UUID albumId,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height) {
        List<Photo> photos = (width != null && height != null) ? photoService.getPhotosByAlbum(albumId, width,
                height) : photoService.getPhotosByAlbum(albumId);
        return photos.stream().map(GetPhotoResponse::from).collect(Collectors.toList());
    }

    @GetMapping("/albums/{album_id}/photos/latest")
    public ResponseEntity<GetPhotoResponse> getLatestPhotoByAlbum(@PathVariable("album_id") UUID albumId,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height) {
        Optional<Photo> photoOptional = (width != null && height != null) ? photoService.getLatestPhotoByAlbum(albumId,
                width, height) : photoService.getLatestPhotoByAlbum(albumId);
        return photoOptional.map(photo -> new ResponseEntity<>(GetPhotoResponse.from(photo), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping("/animals/{animal_id}/photos")
    public List<GetPhotoResponse> getPhotosByAnimal(@PathVariable("animal_id") UUID animalId,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height) {
        List<Photo> photos = (width != null && height != null) ? photoService.getPhotosByAnimal(animalId, width,
                height) : photoService.getPhotosByAnimal(animalId);
        return photos.stream().map(GetPhotoResponse::from).collect(Collectors.toList());
    }

    @GetMapping("/animals/{animal_id}/photos/latest")
    public ResponseEntity<GetPhotoResponse> getLatestPhotoByAnimal(@PathVariable("animal_id") UUID animalId,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height) {
        Optional<Photo> photoOptional = (width != null && height != null) ? photoService.getLatestPhotoByAnimal(
                animalId, width, height) : photoService.getLatestPhotoByAnimal(animalId);
        return photoOptional.map(photo -> new ResponseEntity<>(GetPhotoResponse.from(photo), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping("/photos/{id}")
    public GetPhotoResponse getPhoto(@PathVariable("id") UUID photoId) {
        return GetPhotoResponse.from(photoService.getPhoto(photoId));
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
