package org.dataarc.util.hibernate.type;

import java.sql.Types;

import org.dataarc.core.search.query.SearchQueryObject;
import org.hibernate.usertype.UserType;

// https://github.com/thjanssen/HibernateJSONBSupport/blob/master/PostgresJSONB/src/main/java/org/thoughts/on/java/model/MyJsonType.java
public class SearchJsonType extends AbstractJsonType implements UserType {

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.JAVA_OBJECT };
    }

    @Override
    public Class<SearchQueryObject> returnedClass() {
        return SearchQueryObject.class;
    }

}