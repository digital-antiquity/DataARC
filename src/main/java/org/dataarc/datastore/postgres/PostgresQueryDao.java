package org.dataarc.datastore.postgres;

import java.io.IOException;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.NotImplementedException;
import org.apache.solr.client.solrj.SolrServerException;
import org.dataarc.bean.DataEntry;
import org.dataarc.core.dao.AbstractDao;
import org.dataarc.core.dao.QueryDao;
import org.dataarc.core.query.FilterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class PostgresQueryDao extends AbstractDao implements QueryDao {

    @Autowired
    PostgresDao postgresDao;

    @PersistenceContext
    private EntityManager manager;

    public Map<String, Long> getDistinctValues(String source, String fieldName) throws SolrServerException, IOException {
        return postgresDao.getDistinctValues(source, fieldName);
    }

    public Page<DataEntry> getMatchingRows(FilterQuery fq, int numRows) {
        throw new NotImplementedException();
        /*
         * this "theoretically works, but doesn't because we don't know how to get at the array subtype in a useful way
         * 
         * dataarc=> select id, source, date_created from source_data where data -> 'properties' -> 'sites' ->> 'SiteCode' = 'SITE000572';
         */

        // CriteriaBuilder builder = manager.getCriteriaBuilder();
        // CriteriaQuery<DataEntry> createQuery = builder.createQuery(DataEntry.class);
        // Root<DataEntry> from = createQuery.from(DataEntry.class);
        // Predicate root = builder.equal(from.get("source"), source);
        //
        // for (QueryPart part : fq.getConditions()) {
        // Predicate where = null;
        // String sql = "";
        // logger.debug("{}", part);
        // switch (part.getType()) {
        // case CONTAINS:
        // sql = ""
        // break;
        // // case DOES_NOT_EQUAL:
        // // where.is(part.getValue());
        // // break;
        // case EQUALS:
        // where = where.is(part.getValue());
        // break;
        // case GREATER_THAN:
        // where = where.greaterThan(part.getValue());
        // break;
        // case LESS_THAN:
        // where = where.lessThan(part.getValue());
        // break;
        // default:
        // break;
        // where = Restrictions.sqlRestriction(sql);
        //
        // }
        // logger.debug("{}", where);
        // if (fq.getOperator() == Operator.AND) {
        // root = builder.and(root, );
        // } else {
        // root = builder.or(root, );
        // }
        // }
        // logger.debug("{}", q.getCriteria().toString());
        // Page<DataEntry> find = solrTemplate.queryForPage(q, DataEntry.class);
        // logger.debug("{} ({} total records) ", find, find.getTotalElements());
        // return find;
    }

}
