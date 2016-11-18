package pl.linuh.opening.application;

import chesspresso.pgn.PGNSyntaxError;

/**
 * Created by marek on 19/11/2016.
 */
public class PGNParsingError extends RuntimeException {
    public PGNParsingError(Exception e) {
        super(e);
    }
}
