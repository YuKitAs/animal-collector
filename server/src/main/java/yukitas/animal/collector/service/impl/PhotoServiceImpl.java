package yukitas.animal.collector.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

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
    public List<Photo> getPhotosByAlbum(UUID albumId, int width, int height) {
        return getPhotosByAlbum(albumId).stream()
                .peek(photo -> photo.setContent(convertToThumbnail(photo.getContent(), width, height)))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Photo> getLatestPhotoByAlbum(UUID albumId) {
        List<Photo> photos = getPhotosByAlbum(albumId);
        return photos.isEmpty() ? Optional.empty() : Optional.of(photos.get(0));
    }

    @Override
    public Optional<Photo> getLatestPhotoByAlbum(UUID albumId, int width, int height) {
        Optional<Photo> photoOptional = getLatestPhotoByAlbum(albumId);
        if (photoOptional.isPresent()) {
            Photo photo = photoOptional.get();
            byte[] thumbnailContent = convertToThumbnail(photo.getContent(), width, height);
            photo.setContent(thumbnailContent);
            return Optional.of(photo);
        }
        return Optional.empty();
    }

    @Override
    public List<Photo> getPhotosByAnimal(UUID animalId) {
        return new ArrayList<>(findAnimalById(animalId).getPhotos());
    }

    @Override
    public List<Photo> getPhotosByAnimal(UUID animalId, int width, int height) {
        return getPhotosByAnimal(animalId).stream()
                .peek(photo -> photo.setContent(convertToThumbnail(photo.getContent(), width, height)))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Photo> getLatestPhotoByAnimal(UUID animalId) {
        List<Photo> photos = getPhotosByAnimal(animalId);
        return photos.isEmpty() ? Optional.empty() : Optional.of(photos.get(0));
    }

    @Override
    public Optional<Photo> getLatestPhotoByAnimal(UUID animalId, int width, int height) {
        Optional<Photo> photoOptional = getLatestPhotoByAnimal(animalId);
        if (photoOptional.isPresent()) {
            Photo photo = photoOptional.get();
            byte[] thumbnailContent = convertToThumbnail(photo.getContent(), width, height);
            photo.setContent(thumbnailContent);
            return Optional.of(photo);
        }
        return Optional.empty();
    }

    @Cacheable("photo")
    @Override
    public Photo getPhoto(UUID id) {
        return findPhotoById(id);
    }

    @Override
    public UUID createPhoto(Photo.Builder builder, byte[] content, OffsetDateTime createdAt) {
        LOGGER.debug("Creating photo with original creation time {}", createdAt);

        Photo photo = photoRepository.save(builder.setContent(content)
                .setLocation(new Location(-90 + 180 * new Random().nextDouble(),
                        -180 + 360 * new Random().nextDouble())) // FIXME
                .setCreatedAt(createdAt)
                .build());

        LOGGER.debug("Created photo (id={})", photo.getId());

        return photo.getId();
    }

    @CacheEvict(value = "photo", key = "#id")
    @Override
    public void updatePhoto(UUID id, Set<UUID> animalIds, Set<UUID> albumIds, String description) {
        LOGGER.trace("Updating photo '{}' with [animalIds={}, albumIds={}, description='{}']", id, animalIds, albumIds,
                description);

        Photo photo = photoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));

        updatePhotoAnimals(photo, getValidAnimals(animalIds));
        updatePhotoAlbums(photo, getValidAlbums(albumIds));

        photo.setDescription(description);
        photo.updateLastModified();

        LOGGER.debug("Updated photo (id={}) for animals [{}] and albums [{}]", photo.getId(),
                photo.getAnimals().stream().map(Animal::getName).collect(Collectors.joining(",")),
                photo.getAlbums().stream().map(Album::getName).collect(Collectors.joining(",")));

        photoRepository.save(photo);
    }

    @CacheEvict(value = "photo")
    @Override
    public void deletePhoto(UUID id) {
        findPhotoById(id);

        LOGGER.debug("Deleting photo '{}'", id);

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

    // crop and scale photo based on specified thumbnail width & height
    private byte[] convertToThumbnail(byte[] photoContent, int width, int height) {
        ByteArrayInputStream in = new ByteArrayInputStream(photoContent);
        try {
            BufferedImage original = ImageIO.read(in);

            int originalWidth = original.getWidth();
            int originalHeight = original.getHeight();
            double originalRatio = originalWidth / (double) originalHeight;

            // do nothing when either of the side lengths provided is invalid
            if (width <= 0 || height <= 0 || width > originalWidth || height > originalHeight) {
                LOGGER.warn(String.format("Invalid side length: width=%s, height=%s", width, height));
                return photoContent;
            }

            double ratio = width / (double) height;

            int cropWidth;
            int cropHeight;
            int x = 0;
            int y = 0;

            if (ratio > originalRatio) {
                cropWidth = originalWidth;
                cropHeight = (int) (cropWidth / ratio);
                y = (originalHeight - cropHeight) / 2;
            } else {
                cropHeight = originalHeight;
                cropWidth = (int) (cropHeight * ratio);
                x = (originalWidth - cropWidth) / 2;
            }

            BufferedImage cropped = original.getSubimage(x, y, cropWidth, cropHeight);
            BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            scaled.getGraphics()
                    .drawImage(cropped.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, new Color(0, 0, 0),
                            null);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(scaled, "jpg", out);

            return out.toByteArray();
        } catch (IOException e) {
            LOGGER.error("IOException in scaling photo: {}", e.getMessage());
            return new byte[]{};
        }
    }
}
