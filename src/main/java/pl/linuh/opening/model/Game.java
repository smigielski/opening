package pl.linuh.opening.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by marek on 18/11/2016.
 */
@Entity
public class Game extends BaseEntity {

    public enum Pieces {WHITE, BLACK};

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne
    private Opening opening;

    //TODO make text long enought
    @Basic
    private String pgn;

    //Store enum as string
    private Pieces pieces;

}
