package yukitas.animal.collector.service;

import java.util.List;
import java.util.UUID;

import yukitas.animal.collector.model.Album;

public interface AlbumService {
    List<Album> getAlbumsByCategory(UUID categoryId);

    Album getAlbum(UUID id);

    Album createAlbum(Album album);
}
