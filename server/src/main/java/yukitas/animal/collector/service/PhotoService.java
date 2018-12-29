package yukitas.animal.collector.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import yukitas.animal.collector.model.Location;
import yukitas.animal.collector.model.Photo;

public interface PhotoService {
    List<Photo> getPhotosByAlbum(UUID albumId);

    List<Photo> getPhotosByAnimal(UUID animalId);

    Photo getPhoto(UUID id);

    Photo createPhoto(Photo.Builder builder, Set<UUID> animalIds, Set<UUID> albumIds, byte[] content,
            String description, Location location);

    Photo updatePhoto(UUID id, Set<UUID> animalIds, Set<UUID> albumIds, String description);

    void deletePhoto(UUID id);
}
