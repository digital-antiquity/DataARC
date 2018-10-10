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
import org.dataarc.util.View;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Representation of a "field" in the database. It has both a display name, an internal (import name), and a mongo name as well as allowing it to identify as
 * either a date field or other information
 * 
 * @author abrin
 *
 */
@Entity
@Table(name = "schema_field")
public class SchemaField extends AbstractPersistable {

    private static final long serialVersionUID = -274948984918121197L;

    @Column(length = 100)
    @JsonView(View.Schema.class)
    private String name;

    @Column(length = 100, name = "display_name")
    @JsonView(View.Schema.class)
    private String displayName;

    @Column(length = 100, name = "mongo_name")
    @JsonView(View.Schema.class)
    private String mongoName;

    @Column(name = "field_type")
    @Enumerated(EnumType.STRING)
    @JsonView(View.Schema.class)
    private FieldType type;

    @Column(name = "start_field")
    @JsonView(View.Schema.class)
    private boolean startField;

    @Column(name = "text_date_field")
    @JsonView(View.Schema.class)
    private boolean textDateField;

    @Column(name = "end_field")
    @JsonView(View.Schema.class)
    private boolean endField;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "field_id", nullable = false)
    private Set<Value> values = new HashSet<>();

    public SchemaField() {
    }

    public SchemaField(String field, FieldDataCollector collector) {
        this.name = field;
        this.type = collector.getType(field);
        this.displayName = collector.getDisplayName(field);
        this.mongoName = collector.getDisplayName(field);
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

    public String getMongoName() {
        return mongoName;
    }

    public void setMongoName(String mongoName) {
        this.mongoName = mongoName;
    }

    public boolean isStartField() {
        return startField;
    }

    public void setStartField(boolean startField) {
        this.startField = startField;
    }

    public boolean isEndField() {
        return endField;
    }

    public void setEndField(boolean endField) {
        this.endField = endField;
    }

    public boolean isTextDateField() {
        return textDateField;
    }

    public void setTextDateField(boolean textDateField) {
        this.textDateField = textDateField;
    }

}
