package yukitas.animal.collector.service;

import java.util.List;
import java.util.UUID;

import yukitas.animal.collector.model.Album;

public interface AlbumService {
    List<Album> getAllAlbums();

    List<Album> getAlbumsByCategory(UUID categoryId);

    List<Album> getAlbumsByPhoto(UUID photoId);

    Album getAlbum(UUID id);

    Album createAlbum(UUID categoryId, String name);

    Album updateAlbum(UUID id, String name);

    void deleteAlbum(UUID id);
}
