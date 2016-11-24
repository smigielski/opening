package pl.linuh.opening.application;

import chesspresso.move.IllegalMoveException;

/**
 * Created by marek on 19/11/2016.
 */
public class GameError extends RuntimeException {


    public GameError(String message) {
        super(message);
    }

    public GameError(String message, Exception e) {
        super(message,e);
    }
}
