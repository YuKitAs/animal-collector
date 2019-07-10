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
import yukitas.animal.collector.service.exception.InvalidDataException;
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

        updatePhotoAnimals(photo, getValidAnimals(animalIds));
        updatePhotoAlbums(photo, getValidAlbums(albumIds));

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

    private Animal findAnimalById(UUID id) {
        return animalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("animal", id));
    }

    private Album findAlbumById(UUID id) {
        return albumRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("album", id));
    }

    private Photo findPhotoById(UUID id) {
        return photoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
    }

    private Set<Animal> getValidAnimals(Set<UUID> animalIds) {
        // photo should be associated with at least one animal
        if (animalIds.isEmpty()) {
            throw new RequiredDataNotProvidedException("animal_ids");
        }

        Set<Animal> animals = new HashSet<>(animalRepository.findByIdIn(animalIds));
        if (animals.size() != animalIds.size()) {
            throw new InvalidDataException("animal_ids",
                    animalIds.stream().map(UUID::toString).collect(Collectors.joining(", ")));
        }

        return animals;
    }

    private Set<Album> getValidAlbums(Set<UUID> albumIds) {
        Set<Album> albums = new HashSet<>(albumRepository.findByIdIn(albumIds));
        if (albums.size() != albumIds.size()) {
            throw new InvalidDataException("album_ids",
                    albumIds.stream().map(UUID::toString).collect(Collectors.joining(", ")));
        }

        return albums;
    }

    private void updatePhotoAnimals(Photo photo, Set<Animal> animals) {
        photo.setAnimals(animals);
        animals.forEach(animal -> animal.addPhoto(photo));
    }

    private void updatePhotoAlbums(Photo photo, Set<Album> albums) {
        photo.setAlbums(albums);
        albums.forEach(album -> album.addPhoto(photo));
    }
}
