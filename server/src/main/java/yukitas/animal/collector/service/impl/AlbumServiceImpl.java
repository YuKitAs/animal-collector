package yukitas.animal.collector.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import yukitas.animal.collector.model.Album;
import yukitas.animal.collector.repository.AlbumRepository;
import yukitas.animal.collector.repository.CategoryRepository;
import yukitas.animal.collector.repository.PhotoRepository;
import yukitas.animal.collector.service.AlbumService;
import yukitas.animal.collector.service.exception.EntityNotFoundException;

@Service
public class AlbumServiceImpl implements AlbumService {
    private static final Logger LOGGER = LogManager.getLogger(AlbumServiceImpl.class);
    private static final String ENTITY_NAME = "album";

    private final AlbumRepository albumRepository;
    private final CategoryRepository categoryRepository;
    private final PhotoRepository photoRepository;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository, CategoryRepository categoryRepository,
            PhotoRepository photoRepository) {
        this.albumRepository = albumRepository;
        this.categoryRepository = categoryRepository;
        this.photoRepository = photoRepository;
    }

    @Override
    public List<Album> getAlbumsByCategory(UUID categoryId) {
        categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("category", categoryId));

        return albumRepository.findByCategoryId(categoryId);
    }

    @Override
    public Album getAlbum(UUID id) {
        return findAlbumById(id);
    }

    @Override
    public Album createAlbum(Album album) {
        return albumRepository.save(album);
    }

    @Override
    public Album updateAlbum(UUID id, String name) {
        Album album = findAlbumById(id);

        if (name != null) {
            album.setName(name);
        }

        return albumRepository.save(album);
    }

    @Override
    public void deleteAlbum(UUID id) {
        Album album = findAlbumById(id);
        album.getPhotos().forEach(photo -> {
            photo.removeAlbum(album);
            if (photo.getAlbums().isEmpty()) {
                LOGGER.debug("Pre-remove photo (id={}) which is only associated with album (id={})", photo.getId(), id);
                photoRepository.deleteById(photo.getId());
            }
        });

        LOGGER.debug("Delete album [id={}, name='{}']", id, album.getName());
        albumRepository.deleteById(id);
    }

    private Album findAlbumById(UUID id) {
        return albumRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
    }
}
