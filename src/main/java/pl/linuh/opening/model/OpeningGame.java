package pl.linuh.opening.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by marek on 18/11/2016.
 */
@Entity
public class OpeningGame extends BaseEntity {

    public enum Pieces {white, black};

    @JsonIgnore
    @ManyToOne(optional = false)
    private User user;

    @JsonIgnore
    @ManyToOne(optional = false)
    private Opening opening;

    //TODO make text long enought
    @Basic
    private String pgn;

    //Store enum as string
    @Basic
    private Pieces pieces;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Opening getOpening() {
        return opening;
    }

    public void setOpening(Opening opening) {
        this.opening = opening;
    }

    public String getPgn() {
        return pgn;
    }

    public void setPgn(String pgn) {
        this.pgn = pgn;
    }

    public Pieces getPieces() {
        return pieces;
    }

    public void setPieces(Pieces pieces) {
        this.pieces = pieces;
    }
}
