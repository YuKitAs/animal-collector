package yukitas.animal.collector.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import yukitas.animal.collector.model.Album;
import yukitas.animal.collector.repository.AlbumRepository;
import yukitas.animal.collector.service.AlbumService;
import yukitas.animal.collector.service.exception.EntityNotFoundException;

@Service
public class AlbumServiceImpl implements AlbumService {
    private static final Logger LOGGER = LogManager.getLogger(AlbumServiceImpl.class);

    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Override
    public List<Album> getAlbumsByCategory(UUID categoryId) {
        return albumRepository.findByCategoryId(categoryId);
    }

    @Override
    public Album getAlbum(UUID id) {
        return albumRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Album not found by id=%s", id.toString())));
    }

    @Override
    public Album createAlbum(Album album) {
        return albumRepository.save(album);
    }

    @Override
    public Album updateAlbum(UUID id, String name) {
        Album album = albumRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Album not found by id=%s", id.toString())));

        if (name != null) {
            album.setName(name);
        }

        return albumRepository.save(album);
    }

    @Override
    public void deleteAlbum(UUID id) {
        albumRepository.deleteById(id);
    }
}
