package yukitas.animal.collector.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import yukitas.animal.collector.model.Album;
import yukitas.animal.collector.model.Category;
import yukitas.animal.collector.model.Photo;
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
    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    @Override
    public List<Album> getAlbumsByCategory(UUID categoryId) {
        // Only to check if categoryId exists
        findCategoryById(categoryId);

        return albumRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Album> getAlbumsByPhoto(UUID photoId) {
        return albumRepository.findAll()
                .stream()
                .filter(album -> album.getPhotos()
                        .stream()
                        .map(Photo::getId)
                        .collect(Collectors.toSet())
                        .contains(photoId))
                .collect(Collectors.toList());
    }

    @Cacheable("album")
    @Override
    public Album getAlbum(UUID id) {
        return findAlbumById(id);
    }

    @Override
    public Album createAlbum(UUID categoryId, String name) {
        LOGGER.trace("Creating album with [name='{}'] for category '{}'", name, categoryId);
        return albumRepository.save(
                new Album.Builder().setCategory(findCategoryById(categoryId)).setName(name).build());
    }

    @CacheEvict(value = "album", key = "#id")
    @Override
    public Album updateAlbum(UUID id, String name) {
        LOGGER.trace("Updating album '{}' with [name='{}']", id, name);

        Album album = findAlbumById(id);

        if (name != null && !name.isBlank()) {
            album.setName(name);
        }

        return albumRepository.save(album);
    }

    @CacheEvict(value = "album")
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

        LOGGER.debug("Deleting album [id={}, name='{}']", id, album.getName());
        albumRepository.deleteById(id);
    }

    private Album findAlbumById(UUID id) {
        return albumRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
    }

    private Category findCategoryById(UUID id) {
        // Not using categoryService#findCategoryById mainly because it would cause circular dependencies
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("category", id));
    }
}
