package org.dataarc.bean.schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.dataarc.bean.AbstractPersistable;

@Entity
@Table(name = "field_value")
public class Value extends AbstractPersistable {

    private static final long serialVersionUID = -6506478293196706253L;

    public static final int VALUE_LENGTH = 100;

    @Column(length = VALUE_LENGTH, name = "field_value")
    private String value;

    @Column
    private Integer occurrence;

    public Value() {
    }

    public Value(String value, Integer ocur) {
        this.value = value;
        this.occurrence = ocur;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(Integer ocur) {
        this.occurrence = ocur;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", value, occurrence);
    }
}
