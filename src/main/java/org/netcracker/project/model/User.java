package org.netcracker.project.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Data
public class User /* implements UserDetails */ {
    private String name;
    private String surname;
    private String secName;
    private String email;
    private String password;
    private String login;
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
