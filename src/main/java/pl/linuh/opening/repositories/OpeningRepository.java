package pl.linuh.opening.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.linuh.opening.model.Opening;

/**
 * Created by marek on 07/04/16.
 */
@Repository
public interface OpeningRepository extends CrudRepository<Opening, Long> {
    public Opening findByUserAndName(String username, String name);
}
