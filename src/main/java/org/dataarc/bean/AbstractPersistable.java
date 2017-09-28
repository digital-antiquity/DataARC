package org.dataarc.bean;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.dataarc.util.PersistableUtils;
import org.dataarc.util.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * Convenience base class for Persistable entities providing JPA annotated
 * fields for ID and a property-aware equals()/hashCode() implementations.
 */
@MappedSuperclass
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public abstract class AbstractPersistable implements Serializable {

    private static final long serialVersionUID = -478523777995582558L;

    @Transient
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Uses GenerationType.IDENTITY, which translates to the (big)serial column type for
     * hibernate+postgres, i.e., one sequence table per entity type
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(View.Indicator.class)
    private Long id = -1L;

    @XmlAttribute(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlTransient
    @JsonIgnore
    public boolean isTransient() {
        return PersistableUtils.isTransient(this);
    }

    /**
     * Returns true if:
     * <ol>
     * <li>object identity holds
     * <li>both object types are consistent and the ids for both this persistable object and the incoming persistable object are .equals()
     * <li>OR both object types are consistent and all of the class-specific equality fields specified in getEqualityFields() are .equals()
     * </ol>
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            logger.trace("exact equals {},{}", this, object);
            return true;
        }
        if ((object instanceof AbstractPersistable) && getClass().isInstance(object)) {
            logger.trace("same class {},{}", this, object);
            return PersistableUtils.isEqual(this, getClass().cast(object));
        }
        logger.trace("!! not equals {},{}", this, object);
        return false;
    }

    /**
     * Returns a sensible hashCode() for persisted objects. For transient/unsaved objects, uses
     * the default Object.hashCode().
     */
    @Override
    public int hashCode() {
        Logger logger = LoggerFactory.getLogger(getClass());
        int hashCode = PersistableUtils.toHashCode(this);
        if (logger.isTraceEnabled()) {
            Object[] obj = { hashCode, getClass().getSimpleName(), getId() };
            logger.trace("setting hashCode to {} ({}) {}", obj);
        }
        return hashCode;
    }

}
