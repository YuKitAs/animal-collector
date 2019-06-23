package yukitas.animal.collector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import yukitas.animal.collector.controller.dto.CreateAlbumRequest;
import yukitas.animal.collector.model.Album;
import yukitas.animal.collector.service.AlbumService;

@RestController
public class AlbumController {
    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/albums")
    public ResponseEntity<List<Album>> getAllAlbums() {
        return new ResponseEntity<>(albumService.getAllAlbums(), HttpStatus.OK);
    }

    @GetMapping("/categories/{cat_id}/albums")
    public ResponseEntity<List<Album>> getAlbums(@PathVariable("cat_id") UUID categoryId) {
        return new ResponseEntity<>(albumService.getAlbumsByCategory(categoryId), HttpStatus.OK);
    }

    @GetMapping("/albums/{id}")
    public ResponseEntity<Album> getAlbum(@PathVariable("id") UUID albumId) {
        return new ResponseEntity<>(albumService.getAlbum(albumId), HttpStatus.OK);
    }

    @PostMapping("/categories/{cat_id}/albums")
    public ResponseEntity<Album> createAlbum(@PathVariable("cat_id") UUID categoryId,
            @Valid @RequestBody CreateAlbumRequest createAlbumRequest) {
        return new ResponseEntity<>(albumService.createAlbum(categoryId, createAlbumRequest.getName()),
                HttpStatus.CREATED);
    }

    @PutMapping("/albums/{id}")
    public ResponseEntity<Album> updateAlbum(@PathVariable("id") UUID albumId,
            @Valid @RequestBody CreateAlbumRequest createAlbumRequest) {
        return new ResponseEntity<>(albumService.updateAlbum(albumId, createAlbumRequest.getName()), HttpStatus.OK);
    }

    @DeleteMapping("/albums/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable("id") UUID albumId) {
        albumService.deleteAlbum(albumId);
    }
}
