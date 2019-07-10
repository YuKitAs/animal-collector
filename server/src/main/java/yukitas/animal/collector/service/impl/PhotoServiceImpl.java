package yukitas.animal.collector.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import yukitas.animal.collector.model.Album;
import yukitas.animal.collector.model.Animal;
import yukitas.animal.collector.model.Location;
import yukitas.animal.collector.model.Photo;
import yukitas.animal.collector.repository.AlbumRepository;
import yukitas.animal.collector.repository.AnimalRepository;
import yukitas.animal.collector.repository.PhotoRepository;
import yukitas.animal.collector.service.PhotoService;
import yukitas.animal.collector.service.exception.EntityNotFoundException;
import yukitas.animal.collector.service.exception.RequiredDataNotProvidedException;

@Service
public class PhotoServiceImpl implements PhotoService {
    private static final Logger LOGGER = LogManager.getLogger(PhotoServiceImpl.class);
    private static final String ENTITY_NAME = "photo";

    private final PhotoRepository photoRepository;
    private final AnimalRepository animalRepository;
    private final AlbumRepository albumRepository;

    @Autowired
    public PhotoServiceImpl(PhotoRepository photoRepository, AnimalRepository animalRepository,
            AlbumRepository albumRepository) {
        this.photoRepository = photoRepository;
        this.animalRepository = animalRepository;
        this.albumRepository = albumRepository;
    }

    @Override
    public List<Photo> getPhotosByAlbum(UUID albumId) {
        return new ArrayList<>(findAlbumById(albumId).getPhotos());
    }

    @Override
    public Optional<Photo> getLatestPhotoByAlbum(UUID albumId) {
        List<Photo> photos = getPhotosByAlbum(albumId);
        return photos.isEmpty() ? Optional.empty() : Optional.of(photos.get(0));
    }

    @Override
    public List<Photo> getPhotosByAnimal(UUID animalId) {
        return new ArrayList<>(findAnimalById(animalId).getPhotos());
    }

    @Override
    public Optional<Photo> getLatestPhotoByAnimal(UUID animalId) {
        List<Photo> photos = getPhotosByAnimal(animalId);
        return photos.isEmpty() ? Optional.empty() : Optional.of(photos.get(0));
    }

    @Override
    public Photo getPhoto(UUID id) {
        return findPhotoById(id);
    }

    @Override
    public UUID createPhoto(Photo.Builder builder, byte[] content) {
        LOGGER.trace("Creating photo...");

        Photo photo = photoRepository.save(builder.setContent(content)
                .setLocation(new Location(-90 + 180 * new Random().nextDouble(),
                        -180 + 360 * new Random().nextDouble())) // FIXME
                .build());

        LOGGER.debug("Created photo (id={})", photo.getId());

        return photo.getId();
    }

    @Override
    public void updatePhoto(UUID id, Set<UUID> animalIds, Set<UUID> albumIds, String description) {
        LOGGER.trace("Updating photo '{}' with [animalIds={}, albumIds={}, description='{}']", id, animalIds, albumIds,
                description);

        Photo photo = photoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));

        // photo should be associated with at least one animal
        if (animalIds.isEmpty()) {
            throw new RequiredDataNotProvidedException("animal_ids");
        }

        Set<Animal> animals = getAnimalsById(animalIds);
        photo.setAnimals(animals);
        animals.forEach(animal -> animal.addPhoto(photo));

        Set<Album> albums = getAlbumsById(albumIds);
        photo.setAlbums(albums);
        albums.forEach(album -> album.addPhoto(photo));

        photo.setDescription(description);

        LOGGER.debug("Updated photo (id={}) for animals [{}] and albums [{}]", photo.getId(),
                photo.getAnimals().stream().map(Animal::getName).collect(Collectors.joining(",")),
                photo.getAlbums().stream().map(Album::getName).collect(Collectors.joining(",")));

        photoRepository.save(photo);
    }

    @Override
    public void deletePhoto(UUID id) {
        findPhotoById(id);

        photoRepository.deleteById(id);
    }

    private Set<Animal> getAnimalsById(Set<UUID> animalIds) {
        return animalIds == null ? Collections.emptySet() : animalIds.stream()
                .map(this::findAnimalById)
                .collect(Collectors.toSet());
    }

    private Set<Album> getAlbumsById(Set<UUID> albumIds) {
        return albumIds == null ? Collections.emptySet() : albumIds.stream()
                .map(this::findAlbumById)
                .collect(Collectors.toSet());
    }

    private Animal findAnimalById(UUID id) {
        return animalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("animal", id));
    }

    private Album findAlbumById(UUID id) {
        return albumRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("album", id));
    }

    private Photo findPhotoById(UUID id) {
        return photoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
    }
}
