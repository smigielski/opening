package pl.linuh.opening.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.linuh.opening.model.Game;

/**
 * Created by marek on 07/04/16.
 */
@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

}
