package org.dataarc.solr;

import java.util.Map;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.dao.AbstractDao;
import org.dataarc.core.dao.QueryDao;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.query.Operator;
import org.dataarc.core.query.QueryPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class SolrQueryDao extends AbstractDao implements QueryDao {

    @Autowired
    SourceRepository sourceRepository;
    // http://schinckel.net/2014/05/25/querying-json-in-postgres/
    // http://stormatics.com/howto-use-json-functionality-in-postgresql/
    // https://www.postgresql.org/docs/current/static/datatype-json.html

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    SolrDao solrDao;

    @Override
    public Map<String, Long> getDistinctValues(String source, String fieldName) throws Exception {
        return solrDao.getDistinctValues(source, fieldName);
    }

    @Override
    public Iterable<DataEntry> getMatchingRows(String source, FilterQuery fq) {
        Query q = new SimpleQuery();
        Criteria group = new Criteria("source").is(source);

        for (QueryPart part : fq.getConditions()) {
            Criteria where = new Criteria(part.getFieldName());
            logger.debug("{}", part);
            switch (part.getType()) {
                case CONTAINS:
                    where = where.contains(part.getValue());
                    break;
                // case DOES_NOT_EQUAL:
                // where.is(part.getValue());
                // break;
                case EQUALS:
                    where = where.is(part.getValue());
                    break;
                case GREATER_THAN:
                    where = where.greaterThan(part.getValue());
                    break;
                case LESS_THAN:
                    where = where.lessThan(part.getValue());
                    break;
                default:
                    break;
            }
            logger.debug("{}", where);
            if (fq.getOperator() == Operator.AND) {
                group = group.and(where);
            } else {
                group = group.or(where);
            }
        }
        q.addCriteria(group);
        logger.debug("{}", q.getCriteria().toString());
        Page<DataEntry> find = solrTemplate.queryForPage(q, DataEntry.class);
        logger.debug("{} ({} total records) ", find, find.getTotalElements());
        return find;
    }

}