package pl.linuh.opening.application;

import chesspresso.Chess;
import chesspresso.game.Game;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;
import chesspresso.pgn.PGNWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.linuh.opening.model.Opening;
import pl.linuh.opening.model.OpeningGame;
import pl.linuh.opening.model.User;
import pl.linuh.opening.repositories.GameRepository;
import pl.linuh.opening.repositories.OpeningRepository;
import pl.linuh.opening.repositories.UserRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static pl.linuh.opening.model.OpeningGame.Pieces.black;
import static pl.linuh.opening.model.OpeningGame.Pieces.white;

/**
 * @author marek on 24/10/15.
 */
@RestController
public class OpeningChallenger {

    private static final Logger log = LoggerFactory.getLogger(OpeningChallenger.class);

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

        Opening opening = openingRepository.findByUserAndName(user, openingName);
        assert opening != null;
        openingGame.setOpening(opening);

        openingGame = gameRepository.save(openingGame);

        httpServletResponse.setHeader("Location", "/api/v1/" + username + "/openings/" + openingName + "/games/"
                + openingGame.getId() + "/" + (openingGame.getPieces() == white ? 0 : 1));
    }

    //api/v1/marek/openings/c3 sicicilian/games/13/1,1,1,2,1,1
    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}/games/{gameId}/{ply}")
    public OpeningGame getPosition(@PathVariable("username") String username,
                                   @PathVariable("openingName") String openingName,
                                   @PathVariable("gameId") long gameId,
                                   @PathVariable("ply") int ply) {
        return getPosition(username, openingName, gameId, ply, null);
    }

    //api/v1/marek/openings/c3 sicicilian/games/13/1,1,1,2,1,1
    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}/games/{gameId}/{ply}/{variation}")
    public OpeningGame getPosition(@PathVariable("username") String username,
                                   @PathVariable("openingName") String openingName,
                                   @PathVariable("gameId") long gameId,
                                   @PathVariable("ply") int ply,
                                   @PathVariable("variation") String variation) {

        OpeningGame game = gameRepository.findOne(gameId);
        assert game != null;
        assert game.getOpening().getName().equals(openingName);
        assert game.getUser().getUsername().equals(username);

        String[] variations = variation != null ? variation.split(",") : new String[0];
        int variationLevel = 0;


        try {
            Game currentGame = loadGame(game.getPgn());

            Game openingGame = loadGame(game.getOpening().getPgn());

            int currentPly = 0;


            while (currentPly < ply) {
                if (currentGame.hasNextMove()) {
                    currentGame.goForward();

                    Move[] possibleMoves = openingGame.getNextMoves();
                    if (possibleMoves.length > 1) {
                        openingGame.goForward(Integer.valueOf(variations[variationLevel++]));
                    } else {
                        openingGame.goForward();
                    }


                    if (currentGame.getPosition().getFEN() != openingGame.getPosition().getFEN()) {
                        throw new GameError("Game and opening mismatched");
                    }

                } else {
                    if ((currentGame.getPosition().getToPlay() == Chess.WHITE && game.getPieces() == black) ||
                            (currentGame.getPosition().getToPlay() == Chess.BLACK && game.getPieces() == white)
                            ) {


                        short[] possibleMoves = openingGame.getNextShortMoves();

                        if (possibleMoves.length > 1) {
                            currentGame.getPosition().doMove(possibleMoves[Integer.valueOf(variations[variationLevel++])]);
                        } else {
                            currentGame.getPosition().doMove(possibleMoves[0]);
                        }


                    } else {
                        throw new GameError("Forward not allowed");
                    }
                }
                currentPly++;
            }



            game.setPgn(getPgn(currentGame));
            //return FEN for the move
            return game;


        } catch (IllegalMoveException e) {
            throw new PGNParsingError(e);
        }

    }
    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}/games/{gameId}/{ply}", method = RequestMethod.POST)
    public void checkMove(HttpServletResponse httpServletResponse,
                          @RequestBody String move,
                          @PathVariable("username") String username,
                          @PathVariable("openingName") String openingName,
                          @PathVariable("gameId") long gameId,
                          @PathVariable("ply") int ply){
        checkMove(httpServletResponse,move,username,openingName,gameId,ply,null);
    }

    @RequestMapping(value = "/api/v1/{username}/openings/{openingName}/games/{gameId}/{ply}/{variation}", method = RequestMethod.POST)
    public void checkMove(HttpServletResponse httpServletResponse,
                          @RequestBody String move,
                          @PathVariable("username") String username,
                          @PathVariable("openingName") String openingName,
                          @PathVariable("gameId") long gameId,
                          @PathVariable("ply") int ply,
                          @PathVariable("variation") String variation) {

        OpeningGame game = gameRepository.findOne(gameId);
        assert game != null;
        assert game.getOpening().getName().equals(openingName);
        assert game.getUser().getUsername().equals(username);

        String[] variations = variation != null ? variation.split(",") : new String[0];
        int variationLevel = 0;



        try {
            Game currentGame = loadGame(game.getPgn());

            Game openingGame = loadGame(game.getOpening().getPgn());

            int currentPly = 0;


            while (currentPly < ply && currentGame.hasNextMove()) {
                currentGame.goForward();

                Move[] possibleMoves = openingGame.getNextMoves();
                if (possibleMoves.length > 1) {
                    openingGame.goForward(Integer.valueOf(variations[variationLevel++]));
                } else {
                    openingGame.goForward();
                }


                if (currentGame.getPosition().getFEN() != openingGame.getPosition().getFEN()) {
                    throw new GameError("Game and opening mismatched");
                }

                currentPly++;
            }

            if ((currentGame.getPosition().getToPlay() == Chess.WHITE && game.getPieces() == white) ||
                    (currentGame.getPosition().getToPlay() == Chess.BLACK && game.getPieces() == black)
                    ) {


                Move[] possibleMoves = openingGame.getNextMoves();

                int moveVariation = containsMove(move, possibleMoves);

                if (moveVariation>=0){
                    currentGame.getPosition().doMove(possibleMoves[moveVariation]);
                    game.setPgn(getPgn(currentGame));
                    gameRepository.save(game);
                    httpServletResponse.setHeader("Location", "/api/v1/" + username + "/openings/" + openingName + "/games/"
                            + game.getId() + "/" + (ply+2));

                } else {
                    //TODO 404
                }






            } else {
                throw new GameError("Forward not allowed");
            }


        } catch (IllegalMoveException e) {
            throw new PGNParsingError(e);
        }

    }

    private Game loadGame(String pgn) {
        if (pgn == null) {
            return new Game();
        }

        try {
            PGNReader gameReader = new PGNReader(new StringReader(pgn), "game.pgn");
            Game currentGame = gameReader.parseGame();
            currentGame.gotoStart();
            return currentGame;

        } catch (PGNSyntaxError | IOException exception) {
            throw new PGNParsingError(exception);
        }

    }

    private int containsMove(String move, Move[] moves){
        for (int i = 0; i<moves.length;i++){
            if (moves[i].getSAN().equals(move)){
                return i;
            }
        }
        return -1;
    }

    private String getPgn(Game game){
        StringWriter stringWriter = new StringWriter();
        PGNWriter writer = new PGNWriter(stringWriter);
        writer.write(game.getModel());
        return stringWriter.toString();
    }

}
