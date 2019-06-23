package yukitas.animal.collector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public List<Album> getAllAlbums() {
        return albumService.getAllAlbums();
    }

    @GetMapping("/categories/{cat_id}/albums")
    public List<Album> getAlbums(@PathVariable("cat_id") UUID categoryId) {
        return albumService.getAlbumsByCategory(categoryId);
    }

    @GetMapping("/albums/{id}")
    public Album getAlbum(@PathVariable("id") UUID albumId) {
        return albumService.getAlbum(albumId);
    }

    @PostMapping("/categories/{cat_id}/albums")
    @ResponseStatus(HttpStatus.CREATED)
    public Album createAlbum(@PathVariable("cat_id") UUID categoryId,
            @Valid @RequestBody CreateAlbumRequest createAlbumRequest) {
        return albumService.createAlbum(categoryId, createAlbumRequest.getName());
    }

    @PutMapping("/albums/{id}")
    public Album updateAlbum(@PathVariable("id") UUID albumId,
            @Valid @RequestBody CreateAlbumRequest createAlbumRequest) {
        return albumService.updateAlbum(albumId, createAlbumRequest.getName());
    }

    @DeleteMapping("/albums/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable("id") UUID albumId) {
        albumService.deleteAlbum(albumId);
    }
}
