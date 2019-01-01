package yukitas.animal.collector.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import yukitas.animal.collector.model.Category;

@Repository
public interface CategoryRepository extends CrudRepository<Category, UUID> {
    List<Category> findAll();
}
