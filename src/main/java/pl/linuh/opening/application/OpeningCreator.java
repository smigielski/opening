package pl.linuh.opening.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.linuh.opening.model.User;
import pl.linuh.opening.repositories.OpeningRepository;
import pl.linuh.opening.model.Opening;
import pl.linuh.opening.repositories.UserRepository;

/**
 * @author marek on 24/10/15.
 */
@RestController
public class OpeningCreator {

    @Autowired
    private OpeningRepository openingRepository;

    @Autowired
    private UserRepository userRepository;

    private boolean autocreate = true;

    //list all openings
    @RequestMapping(value = "/api/v1/{username}/openings")
    public Iterable<Opening> getOpenings(@PathVariable("username") String username) {
        return openingRepository.findAll();
    }

    //create new opening?
    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}",method = RequestMethod.PUT)
    public Opening storeOpening(Opening opening, @PathVariable("username") String username, @PathVariable("openingName") String openingName) {
        User user = userRepository.findByUsername(username);
        if (user == null && autocreate){
            user = new User(username);
        }
        if (opening.getId()==0){
            opening.setUser(user);
        }
        return openingRepository.save(opening);
    }

    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}")
    public Opening getOpening(@PathVariable("username") String username, @PathVariable("openingName") String openingName) {
        return openingRepository.findByUserAndName(username,openingName);
    }

    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}", method = RequestMethod.DELETE)
    public void deleteOpening(@PathVariable("username") String username, @PathVariable("openingName") String openingName) {
        Opening opening = openingRepository.findByUserAndName(username,openingName);
        openingRepository.delete(opening);
    }



}
