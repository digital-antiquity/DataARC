package org.dataarc.bean.schema;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.dataarc.bean.AbstractPersistable;

@Entity
public class Schema extends AbstractPersistable {

    @Column(length=100)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name="schema_id",nullable = false)
    private Set<Field> fields = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String source) {
        this.name = source;
    }

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

}
