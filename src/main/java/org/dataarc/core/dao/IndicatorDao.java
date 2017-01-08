package org.dataarc.core.dao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringEscapeUtils;
import org.dataarc.bean.Indicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IndicatorDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager manager;

    public void save(Indicator indicator) {
        manager.persist(indicator);
    }

    public void deleteAll() {
        manager.createQuery("delete from Indicator").executeUpdate();
    }

    public Set<String> findAll() {
        return manager.createQuery("from Indicator", Indicator.class).getResultList().stream()
                .map(schema -> schema.getName())
                .collect(Collectors.toSet());
    }

    public Indicator findById(Long id) {
        Query query = manager.createQuery("from Indicator where id=:id", Indicator.class);
        query.setParameter("id", id);
        return (Indicator) query.getSingleResult();
    }

    public List<Indicator> findAllForSchema(String schemaName) {
        // FIXME: SQL-inject
        Query query = manager.createNativeQuery("select * from indicator where query @> '{\"schema\":\"" + StringEscapeUtils.escapeSql(schemaName) + "\" }'",
                Indicator.class);
        // query.setParameter(1, schemaName);
        return query.getResultList();
    }

    public Indicator merge(Indicator indicator) {
        return manager.merge(indicator);

    }

}
