package pl.linuh.opening.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.linuh.opening.model.Opening;
import pl.linuh.opening.repositories.UserRepository;
import pl.linuh.opening.model.User;
import pl.linuh.opening.repositories.OpeningRepository;

/**
 * @author marek on 24/10/15.
 */
@RestController
public class OpeningCreator {

    @Autowired
    private OpeningRepository openingRepository;

    @Autowired
    private UserRepository userRepository;


    //list all openings
    @RequestMapping(value = "/api/v1/{username}/openings")
    public Iterable<Opening> getOpenings(@PathVariable("username") String username) {
        return openingRepository.findAll();
    }

    //create new opening?
    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}",method = RequestMethod.PUT)
    public Opening storeOpening(@RequestBody Opening opening, @PathVariable("username") String username, @PathVariable("openingName") String openingName) {
        assert openingName!=null && openingName.equals(opening.getName());

        User user = userRepository.findByUsername(username);
        if (user == null){
            //TODO add usermanagment
            user = new User(username);
            userRepository.save(user);
        }
        opening.setUser(user);

        return openingRepository.save(opening);
    }

    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}")
    public Opening getOpening(@PathVariable("username") String username, @PathVariable("openingName") String openingName) {
        User user = userRepository.findByUsername(username);
        return openingRepository.findByUserAndName(user,openingName);
    }

    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}", method = RequestMethod.DELETE)
    public void deleteOpening(@PathVariable("username") String username, @PathVariable("openingName") String openingName) {
        User user = userRepository.findByUsername(username);
        Opening opening = openingRepository.findByUserAndName(user,openingName);
        openingRepository.delete(opening);
    }



}
