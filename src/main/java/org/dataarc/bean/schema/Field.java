package org.dataarc.bean.schema;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.dataarc.bean.AbstractPersistable;

@Entity
@Table(name = "schema_field")
public class Field extends AbstractPersistable {

    @Column(length = 100)
    private String name;

    @Column(name = "field_type")
    @Enumerated(EnumType.STRING)
    private FieldType type;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "field_id", nullable = false)
    private Set<Value> values = new HashSet<>();

    public Field() {
    }

    public Field(String field, FieldType type) {
        this.name = field;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public Set<Value> getValues() {
        return values;
    }

    public void setValues(Set<Value> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return String.format("%s (%s - %s)", name, type, getId());
    }
}
