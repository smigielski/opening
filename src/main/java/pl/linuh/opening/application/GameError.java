package pl.linuh.opening.application;

/**
 * Created by marek on 19/11/2016.
 */
public class GameError extends RuntimeException {


    public GameError(String message) {
        super(message);
    }
}
