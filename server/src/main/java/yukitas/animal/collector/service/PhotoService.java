package yukitas.animal.collector.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import yukitas.animal.collector.model.Photo;

public interface PhotoService {
    List<Photo> getPhotosByAlbum(UUID albumId);

    Photo getLatestPhotoByAlbum(UUID albumId);

    List<Photo> getPhotosByAnimal(UUID animalId);

    Photo getLatestPhotoByAnimal(UUID animalId);

    Photo getPhoto(UUID id);

    UUID createPhoto(Photo.Builder builder, byte[] content);

    void updatePhoto(UUID id, Set<UUID> animalIds, Set<UUID> albumIds, String description);

    void deletePhoto(UUID id);
}
