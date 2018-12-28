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
        Set<Animal> animals = animalIds.stream().map(animalService::getAnimal).collect(Collectors.toSet());
        Set<Album> albums = albumIds.stream().map(albumService::getAlbum).collect(Collectors.toSet());

        Photo photo = photoRepository.save(builder.setAnimals(animals)
                .setAlbums(albums)
                .setContent(content)
                .setDescription(description)
                .setLocation(location)
                .build());

        LOGGER.debug("Created photo for animals [{}] and albums [{}]",
                photo.getAnimals().stream().map(Animal::getName).collect(Collectors.joining(",")),
                photo.getAlbums().stream().map(Album::getName).collect(Collectors.joining(",")));

        animals.forEach(animal -> animal.addPhoto(photo));
        albums.forEach(album -> album.addPhoto(photo));

        return photo;
    }

    @Override
    public void deletePhoto(UUID id) {
        photoRepository.deleteById(id);
    }
}
