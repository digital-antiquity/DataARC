package org.dataarc.bean;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.dataarc.core.query.FilterQuery;
import org.dataarc.util.hibernate.type.QueryJsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@Entity
@TypeDefs({ @TypeDef(name = "QueryJsonObject", typeClass = QueryJsonType.class) })
public class Indicator extends AbstractPersistable {

    @Column(length = 100)
    private String name;
    
    @Column
    @Type(type = "QueryJsonObject")
    private FilterQuery query;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FilterQuery getQuery() {
        return query;
    }

    public void setQuery(FilterQuery query) {
        this.query = query;
    }

}
