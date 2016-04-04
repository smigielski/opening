package pl.linuh.opening;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author marek on 24/10/15.
 */
@RestController
public class OpeningChallenger {


    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}/challenge",method = RequestMethod.DELETE)
    public void clearCurrentGame(@PathVariable("username") String username, @PathVariable("openingName") String openingName) {

    }

    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}/challenge")
    public String getMove() {
        return "";
    }

    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}/challenge", method = RequestMethod.POST)
    public void playMove(String move) {
        
    }




}
