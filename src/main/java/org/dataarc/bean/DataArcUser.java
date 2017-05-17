package org.dataarc.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "user")
public class DataArcUser extends AbstractPersistable {

    private static final long serialVersionUID = 8179515553625706719L;

    @Column(unique = true)
    private String username;

    @Column(name = "first_name", length = 255)
    private String firstName;
    @Column(name = "last_name", length = 255)
    private String lastName;
    @Column(name = "institution", length = 1024)
    private String institution;
    @Column(name = "email", length = 523)
    private String email;
    @Column(name = "admin")
    private boolean admin;
    @Column(name = "date_created")
    private Date dateCreated;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

}
