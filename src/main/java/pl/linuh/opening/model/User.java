package pl.linuh.opening.model;

import javax.persistence.Basic;
import javax.persistence.Entity;

/**
 * Created by marek on 18/11/2016.
 */
@Entity
public class User extends BaseEntity {

    @Basic
    private String username;

    public User(){

    }

    public User(String username) {
        this.username=username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
