package yukitas.animal.collector.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import yukitas.animal.collector.model.Album;
import yukitas.animal.collector.model.Animal;
import yukitas.animal.collector.model.Location;
import yukitas.animal.collector.model.Photo;
import yukitas.animal.collector.repository.PhotoRepository;
import yukitas.animal.collector.service.AlbumService;
import yukitas.animal.collector.service.AnimalService;
import yukitas.animal.collector.service.PhotoService;
import yukitas.animal.collector.service.exception.EntityNotFoundException;

@Service
public class PhotoServiceImpl implements PhotoService {
    private static final Logger LOGGER = LogManager.getLogger(PhotoServiceImpl.class);

    private final PhotoRepository photoRepository;
    private final AlbumService albumService;
    private final AnimalService animalService;

    @Autowired
    public PhotoServiceImpl(PhotoRepository photoRepository, AlbumService albumService, AnimalService animalService) {
        this.photoRepository = photoRepository;
        this.albumService = albumService;
        this.animalService = animalService;
    }

    @Override
    public List<Photo> getPhotosByAlbum(UUID albumId) {
        return new ArrayList<>(albumService.getAlbum(albumId).getPhotos());
    }

    @Override
    public List<Photo> getPhotosByAnimal(UUID animalId) {
        return new ArrayList<>(animalService.getAnimal(animalId).getPhotos());
    }

    @Override
    public Photo getPhoto(UUID id) {
        return photoRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Photo not found by id=%s", id.toString())));
    }

    @Override
    public Photo createPhoto(Photo.Builder builder, Set<UUID> animalIds, Set<UUID> albumIds, byte[] content,
            String description, Location location) {
        Set<Animal> animals = getAnimalsById(animalIds);
        Set<Album> albums = getAlbumsById(albumIds);

        Photo photo = photoRepository.save(builder.setAnimals(animals)
                .setAlbums(albums)
                .setContent(content)
                .setDescription(description)
                .setLocation(location)
                .build());

        LOGGER.debug("Created photo (id={}) for animals [{}] and albums [{}]", photo.getId(),
                photo.getAnimals().stream().map(Animal::getName).collect(Collectors.joining(",")),
                photo.getAlbums().stream().map(Album::getName).collect(Collectors.joining(",")));

        animals.forEach(animal -> animal.addPhoto(photo));
        albums.forEach(album -> album.addPhoto(photo));

        return photo;
    }

    @Override
    public Photo updatePhoto(UUID id, Set<UUID> animalIds, Set<UUID> albumIds, String description) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Photo not found by id=%s", id.toString())));

        Set<Animal> animals = getAnimalsById(animalIds);
        Set<Album> albums = getAlbumsById(albumIds);

        if (animals != null) {
            photo.setAnimals(animals);
        }

        if (albums != null) {
            photo.setAlbums(albums);
        }

        if (description != null) {
            photo.setDescription(description);
        }

        LOGGER.debug("Updated photo (id={}) for animals [{}] and albums [{}]", photo.getId(),
                photo.getAnimals().stream().map(Animal::getName).collect(Collectors.joining(",")),
                photo.getAlbums().stream().map(Album::getName).collect(Collectors.joining(",")));

        return photoRepository.save(photo);
    }

    @Override
    public void deletePhoto(UUID id) {
        photoRepository.deleteById(id);
    }

    private Set<Animal> getAnimalsById(Set<UUID> animalIds) {
        return animalIds == null ? null : animalIds.stream().map(animalService::getAnimal).collect(Collectors.toSet());
    }

    private Set<Album> getAlbumsById(Set<UUID> albumIds) {
        return albumIds == null ? null : albumIds.stream().map(albumService::getAlbum).collect(Collectors.toSet());
    }
}
