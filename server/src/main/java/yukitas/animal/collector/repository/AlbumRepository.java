package yukitas.animal.collector.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import yukitas.animal.collector.model.Album;

@Repository
public interface AlbumRepository extends CrudRepository<Album, UUID> {
    List<Album> findAll();

    List<Album> findByCategoryId(UUID categoryId);
}
