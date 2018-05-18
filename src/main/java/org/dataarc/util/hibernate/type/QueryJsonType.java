package org.dataarc.util.hibernate.type;

import org.dataarc.core.query.FilterQuery;
import org.hibernate.usertype.UserType;

// https://github.com/thjanssen/HibernateJSONBSupport/blob/master/PostgresJSONB/src/main/java/org/thoughts/on/java/model/MyJsonType.java
public class QueryJsonType extends AbstractJsonType implements UserType {

    @Override
    public Class<FilterQuery> returnedClass() {
        return FilterQuery.class;
    }

}