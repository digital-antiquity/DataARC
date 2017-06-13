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
import org.dataarc.util.FieldDataCollector;

@Entity
@Table(name = "schema_field")
public class Field extends AbstractPersistable {

    private static final long serialVersionUID = -274948984918121197L;

    @Column(length = 100)
    private String name;

    @Column(length = 100, name ="display_name")
    private String displayName;

    @Column(name = "field_type")
    @Enumerated(EnumType.STRING)
    private FieldType type;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "field_id", nullable = false)
    private Set<Value> values = new HashSet<>();

    public Field() {
    }

    public Field(String field, FieldDataCollector collector) {
        this.name = field;
        this.type = collector.getType(field);
        this.displayName = collector.getDisplayName(field);
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
