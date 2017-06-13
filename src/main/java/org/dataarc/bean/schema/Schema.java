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

    private static final long serialVersionUID = -5169843883562531248L;

    @Column(length = 100)
    private String name;

    @Column(length = 100, name="display_name")
    private String displayName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "schema_id", nullable = false)
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

    
    public Field getFieldByName(String name) {
        for (Field fld : fields) {
            if (fld.getDisplayName().equals(name) || fld.getName().equals(name)) {
                return fld;
            }
        };
        return null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
