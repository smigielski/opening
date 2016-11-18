package pl.linuh.opening.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by marek on 07/04/16.
 */
@Entity
public class Opening extends BaseEntity {

    @ManyToOne(optional = false)
    @JsonIgnore
    private User user;

    @Basic
    private String name;

    @Basic
    private String pgn;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPgn() {
        return pgn;
    }

    public void setPgn(String pgn) {
        this.pgn = pgn;
    }

    @Override
    public String toString() {
        return String.format(
                "Opening[name='%s', pgn='%s']",
                name, pgn);
    }
}
