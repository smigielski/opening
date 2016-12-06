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
import pl.linuh.opening.repositories.UserRepository;
import pl.linuh.opening.model.Opening;
import pl.linuh.opening.model.OpeningGame;
import pl.linuh.opening.model.ResponseMessage;
import pl.linuh.opening.model.User;
import pl.linuh.opening.repositories.GameRepository;
import pl.linuh.opening.repositories.OpeningRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;

import static pl.linuh.opening.model.OpeningGame.Pieces.black;
import static pl.linuh.opening.model.OpeningGame.Pieces.white;

/**
 * @author marek on 24/10/15.
 */
@RestController
public class OpeningChallenger {

    private static final Logger log = LoggerFactory.getLogger(OpeningChallenger.class);

    private static final Random random = new Random();

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private OpeningRepository openingRepository;

    @Autowired
    private UserRepository userRepository;



    @RequestMapping(value = "/v1/{username}/openings/{openingName}/games", method = RequestMethod.POST)
    public void checkMove(HttpServletResponse httpServletResponse, @RequestBody OpeningGame game, @PathVariable("username") String username,
                          @PathVariable("openingName") String openingName) {

        User user = userRepository.findByUsername(username);
        assert user != null;
        game.setUser(user);

        Opening opening = openingRepository.findByUserAndName(user, openingName);
        assert opening != null;
        game.setOpening(opening);



        Game currentGame = loadGame(game.getPgn());

        Game openingGame = loadGame(game.getOpening().getPgn());

        if (game.getPieces()==black) {
            makeMove(currentGame, openingGame);
        }

        game.setPgn(getPgn(currentGame));
        game = gameRepository.save(game);

        httpServletResponse.setHeader("Location", "/api/v1/" + username + "/openings/" + openingName + "/games/"
                + game.getId() + "/" + currentGame.getCurrentMoveNumber());
    }

    //api/v1/marek/openings/c3 sicicilian/games/13/1,1,1,2,1,1
    //api/v1/test/openings/e4/games/1/0
    @RequestMapping(value = "/v1/{username}/openings/{openingName}/games/{gameId}/{ply}")
    public OpeningGame getPosition(@PathVariable("username") String username,
                                   @PathVariable("openingName") String openingName,
                                   @PathVariable("gameId") long gameId,
                                   @PathVariable("ply") int ply) {
        return getPosition(username, openingName, gameId, ply, null);
    }

    //api/v1/marek/openings/c3 sicicilian/games/13/1,1,1,2,1,1
    @RequestMapping(value = "/v1/{username}/openings/{openingName}/games/{gameId}/{ply}/{variation}")
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


                    if (!currentGame.getPosition().getFEN().equals(openingGame.getPosition().getFEN())) {
                        throw new GameError("Game and opening mismatched");
                    }

                } else {
                    throw new GameError("Forward not allowed");
                }
                currentPly++;
            }



            game.setPgn(getPgn(currentGame));
            //return FEN for the move
            return game;

    }
    ///api/v1/test/openings/e4/games/1/0
    @RequestMapping(value = "/v1/{username}/openings/{openingName}/games/{gameId}/{ply}", method = RequestMethod.POST)
    public ResponseMessage.MoveStatus checkMove(HttpServletResponse httpServletResponse,
                          @RequestBody String move,
                          @PathVariable("username") String username,
                          @PathVariable("openingName") String openingName,
                          @PathVariable("gameId") long gameId,
                          @PathVariable("ply") int ply){
        return checkMove(httpServletResponse,move,username,openingName,gameId,ply,null);
    }

    @RequestMapping(value = "/v1/{username}/openings/{openingName}/games/{gameId}/{ply}/{variation}", method = RequestMethod.POST)
    public ResponseMessage.MoveStatus checkMove(HttpServletResponse httpServletResponse,
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




                ResponseMessage.MoveStatus moveStatus;
                if (moveVariation>=0){

                    openingGame.goForward(moveVariation);
                    short[] nags = openingGame.getNags();

                    if (nags!=null && nags.length>0){
                        moveStatus = ResponseMessage.MoveStatus.valueOf(nags[0]);
                    } else {
                        moveStatus = ResponseMessage.MoveStatus.NORMAL_MOVE;
                    }
                    if (moveStatus.isAcceptable()) {
                        currentGame.getPosition().doMove(possibleMoves[moveVariation]);

                        makeMove(currentGame, openingGame);


                        game.setPgn(getPgn(currentGame));
                        gameRepository.save(game);
                    }

                } else {
                    moveStatus = ResponseMessage.MoveStatus.UNKNOW_MOVE;
                }

                return sendResponse(httpServletResponse,"/api/v1/" + username + "/openings/" + openingName + "/games/"
                        + game.getId() + "/" + currentGame.getCurrentPly(), moveStatus);


            } else {
                throw new GameError("Forward not allowed");
            }


        } catch (IllegalMoveException e) {
            throw new PGNParsingError(e);
        }

    }

    private void makeMove(Game currentGame, Game openingGame)  {
        int numberOfLines  = openingGame.getNumOfNextMoves();
        int line = random.nextInt(numberOfLines);
        try {
            currentGame.getPosition().doMove(openingGame.getNextMove(line));
        } catch (IllegalMoveException e) {
            throw new GameError("Illegal move that should not happen",e);
        }
    }

    private Game loadGame(String pgn) {
        if (pgn == null) {
            return new Game();
        }

        try {
            PGNReader gameReader = new PGNReader(new StringReader(pgn), "game.pgn");
            Game currentGame = gameReader.parseGame();
            if (currentGame==null){
                return new Game();
            }
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
        writer.writeMovesOnly(game.getModel());
        return stringWriter.toString();
    }

    private ResponseMessage.MoveStatus sendResponse(HttpServletResponse httpServletResponse,String location,ResponseMessage.MoveStatus status ){

        httpServletResponse.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        httpServletResponse.setHeader("Location",location);

        return status;
    }
}
