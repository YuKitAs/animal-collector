package yukitas.animal.collector.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import yukitas.animal.collector.model.Location;
import yukitas.animal.collector.model.Photo;

public interface PhotoService {
    List<Photo> getPhotosByAlbum(UUID albumId);

    List<Photo> getPhotosByAlbum(UUID albumId, int width, int height);

    Optional<Photo> getLatestPhotoByAlbum(UUID albumId);

    Optional<Photo> getLatestPhotoByAlbum(UUID albumId, int width, int height);

    List<Photo> getPhotosByAnimal(UUID animalId);

    List<Photo> getPhotosByAnimal(UUID animalId, int width, int height);

    Optional<Photo> getLatestPhotoByAnimal(UUID animalId);

    Optional<Photo> getLatestPhotoByAnimal(UUID animalId, int width, int height);

    Photo getPhoto(UUID id);

    UUID createPhoto(Photo.Builder builder, byte[] content, OffsetDateTime createdAt, Location location);

    void updatePhoto(UUID id, Set<UUID> animalIds, Set<UUID> albumIds, String description);

    void deletePhoto(UUID id);
}
