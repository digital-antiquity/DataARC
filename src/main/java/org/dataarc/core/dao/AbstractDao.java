package org.dataarc.core.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public abstract class AbstractDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    // @PersistenceContext
    // private EntityManager manager;

    // public EntityManager getManager() {
    // return manager;
    // }
    //
    // public void setManager(EntityManager manager) {
    // this.manager = manager;
    // }

}
