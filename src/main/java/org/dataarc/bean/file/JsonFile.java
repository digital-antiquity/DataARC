package org.dataarc.bean.file;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.dataarc.bean.AbstractPersistable;

@Entity()
@Table(name="json_file")
public class JsonFile extends AbstractPersistable {

    private static final long serialVersionUID = -2897929979637433543L;

    @Column(length = 100)
    private String name;
    
    @Column(length = 1024)
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
