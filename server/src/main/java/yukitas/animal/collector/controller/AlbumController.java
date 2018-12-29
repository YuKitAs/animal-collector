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
import yukitas.animal.collector.service.CategoryService;

@RestController
public class AlbumController {
    private final AlbumService albumService;
    private final CategoryService categoryService;

    @Autowired
    public AlbumController(AlbumService albumService, CategoryService categoryService) {
        this.albumService = albumService;
        this.categoryService = categoryService;
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
        return new ResponseEntity<>(albumService.createAlbum(CreateAlbumRequest.builder()
                .setName(createAlbumRequest.getName())
                .setCategory(categoryService.getCategory(categoryId))
                .build()), HttpStatus.CREATED);
    }

    @PutMapping("/albums/{id}")
    public ResponseEntity<Album> updateAlbum(@PathVariable("id") UUID albumId,
            @Valid @RequestBody CreateAlbumRequest createAlbumRequest) {
        return new ResponseEntity<>(albumService.updateAlbum(albumId, createAlbumRequest.getName()), HttpStatus.OK);
    }

    @DeleteMapping("/albums/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable("id") UUID albumId) {
        albumService.deleteAlbum(albumId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
