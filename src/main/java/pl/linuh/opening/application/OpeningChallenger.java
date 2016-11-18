package pl.linuh.opening.application;

import chesspresso.game.Game;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;
import chesspresso.pgn.PGNWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.linuh.opening.model.OpeningGame;
import pl.linuh.opening.model.Opening;
import pl.linuh.opening.model.User;
import pl.linuh.opening.repositories.GameRepository;
import pl.linuh.opening.repositories.OpeningRepository;
import pl.linuh.opening.repositories.UserRepository;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static pl.linuh.opening.model.OpeningGame.Pieces.white;

/**
 * @author marek on 24/10/15.
 */
@RestController
public class OpeningChallenger {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private OpeningRepository openingRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}/games", method = RequestMethod.POST)
    public void checkMove(HttpServletResponse httpServletResponse, @RequestBody OpeningGame openingGame, @PathVariable("username") String username,
                          @PathVariable("openingName") String openingName) {

        User user = userRepository.findByUsername(username);
        assert user != null;
        openingGame.setUser(user);

        Opening opening = openingRepository.findByUserAndName(user,openingName);
        assert opening != null;
        openingGame.setOpening(opening);

        openingGame = gameRepository.save(openingGame);

        httpServletResponse.setHeader("Location", "/api/v1/"+username+"/openings/"+openingName+"/games/"
                + openingGame.getId()+ "/" + (openingGame.getPieces()==white?1:2));
    }

    //api/v1/marek/openings/c3 sicicilian/games/13/1,1,1,2,1,1
    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}/games/{gameId}/{ply}")
    public OpeningGame getPosition(@PathVariable("username") String username,
                                   @PathVariable("openingName") String openingName,
                                   @PathVariable("gameId") long gameId,
                                   @PathVariable("ply") int ply) {
        return getPosition(username,openingName,gameId,ply,null);
    }

    //api/v1/marek/openings/c3 sicicilian/games/13/1,1,1,2,1,1
    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}/games/{gameId}/{ply}/{variation}")
    public OpeningGame getPosition(@PathVariable("username") String username,
                                   @PathVariable("openingName") String openingName,
                                   @PathVariable("gameId") long gameId,
                                   @PathVariable("ply") int ply,
                                   @PathVariable("variation") String variation) {
        OpeningGame openingGame = gameRepository.findOne(gameId);
        assert openingGame != null;
        assert openingGame.getOpening().getName().equals(openingName);
        assert openingGame.getUser().getUsername().equals(username);


        PGNReader reader = new PGNReader(new StringReader(openingGame.getPgn()!=null?openingGame.getPgn():""), "game.pgn");
        try {
            Game currentGame = reader.parseGame();

            if (currentGame==null){
                currentGame = new Game();
            }
            //TODO make some moves



            StringWriter stringWriter = new StringWriter();
            PGNWriter writer = new PGNWriter(stringWriter);
            writer.write(currentGame.getModel());
            openingGame.setPgn(stringWriter.toString());
            //return FEN for the move
            return openingGame;


        } catch (PGNSyntaxError pgnSyntaxError) {
            throw new PGNParsingError(pgnSyntaxError);
        } catch (IOException e) {
            throw new PGNParsingError(e);
        }

    }



    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}/games/{gameId}/{ply}/{variation}", method = RequestMethod.POST)
    public void checkMove(String move) {
        //TODO check user is logged in
        //TODO get game
        //TODO verify that plynumber and variation is correct ?should allow backward games from the position?
        //update opening statistics
        //return result () + location of the new board
    }




}
