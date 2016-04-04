package pl.linuh.opening;

import chesspresso.pgn.PGNSyntaxError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author marek on 24/10/15.
 */
@RestController
public class OpeningCreator {


    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}",method = RequestMethod.PUT)
    public void storeOpening(String move, @PathVariable("username") String username, @PathVariable("openingName") String openingName) {

    }

    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}")
    public String getOpening() {
        return "";
    }

    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}", method = RequestMethod.DELETE)
    public void deleteOpening() {

    }




}
