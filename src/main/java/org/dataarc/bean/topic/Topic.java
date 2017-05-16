package org.dataarc.bean.topic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import org.dataarc.bean.AbstractPersistable;

@Entity
public class Topic extends AbstractPersistable {

    private static final long serialVersionUID = 4436249058757559474L;

    @Column(length = 255)
    private String name;

    @Column(length = 255, unique=true)
    @NotNull
    private String identifier;

    @ElementCollection()
    @CollectionTable(name = "topic_name_varients", joinColumns = @JoinColumn(name = "topic_id"))
    @Column(name = "varient")
    private List<String> varients = new ArrayList<>();

//    @OneToMany()
//    private Set<Association> associations = new HashSet<>();

    @ManyToMany(cascade={CascadeType.ALL})
    @JoinTable(name = "topic_parents", joinColumns = { @JoinColumn(nullable = false, name = "topic_id") }, inverseJoinColumns = { @JoinColumn(
            nullable = false,
            name = "parent_id") })
    private Set<Topic> parents = new HashSet<>();

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

    @Override
    public String toString() {
        return String.format("%s (%s)" , name, getId());
    }

    public Set<Topic> getParents() {
        return parents;
    }

    public void setParents(Set<Topic> parents) {
        this.parents = parents;
    }
}
