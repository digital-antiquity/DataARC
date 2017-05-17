package org.dataarc.bean;

import java.util.Date;

import javax.persistence.Entity;

import org.dataarc.util.hibernate.type.QueryJsonType;
import org.dataarc.util.hibernate.type.StateJsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@Entity
@TypeDefs({ @TypeDef(name = "SavedStateJsonObject", typeClass =StateJsonType.class) })
public class State extends AbstractPersistable {

    private Long parentId;
    
    private Date dateCreated;
    
    private String uid;
    
    @Type(type = "SavedStateJsonObject")
    private String payload;
    
    private Long views;
}
