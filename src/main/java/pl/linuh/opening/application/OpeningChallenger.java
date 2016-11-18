package pl.linuh.opening.application;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.linuh.opening.model.Game;

/**
 * @author marek on 24/10/15.
 */
@RestController
public class OpeningChallenger {


    //api/v1/marek/openings/c3 sicicilian/13/1,1,1,2,1,1
    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}/{game}/{ply}/{variation}")
    public Game getPosition() {
        //TODO check user is logged in
        //TODO get opening
        //TODO get game

        //return FEN for the move
        return null;
    }

    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}/{game}/{ply}/{variation}", method = RequestMethod.POST)
    public void checkMove(String move) {
        //TODO check user is logged in
        //TODO get game
        //TODO verify that plynumber and variation is correct ?should allow backward games from the position?
        //update opening statistics
        //return result () + location of the new board
    }




}
