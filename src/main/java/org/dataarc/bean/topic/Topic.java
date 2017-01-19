package org.dataarc.bean.topic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.dataarc.bean.AbstractPersistable;

@Entity
public class Topic extends AbstractPersistable {

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private String identifier;

    @ElementCollection
    private List<String> varients;

    @OneToMany
    private Set<Association> associations = new HashSet<>();

    @ManyToOne(optional = true)
    private Topic parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<String> getVarients() {
        return varients;
    }

    public void setVarients(List<String> varients) {
        this.varients = varients;
    }

    public Topic getParent() {
        return parent;
    }

    public void setParent(Topic parent) {
        this.parent = parent;
    }

}
