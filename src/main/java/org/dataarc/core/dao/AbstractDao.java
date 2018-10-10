package org.dataarc.core.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public abstract class AbstractDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

}
