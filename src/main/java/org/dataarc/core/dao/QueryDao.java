package org.dataarc.core.dao;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.io.IOException;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.client.solrj.response.LukeResponse.FieldInfo;
import org.apache.solr.client.solrj.response.schema.SchemaRepresentation;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.search.facet.FacetRequest;
import org.dataarc.core.query.solr.SourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class QueryDao extends AbstractDao {

    @Autowired
    SourceRepository sourceRepository;
    // http://schinckel.net/2014/05/25/querying-json-in-postgres/
    // http://stormatics.com/howto-use-json-functionality-in-postgresql/
    // https://www.postgresql.org/docs/current/static/datatype-json.html

    @Autowired
    SolrTemplate solrTemplate;
    
    public Map<String, Long> getDistinctValues(String fieldName) throws SolrServerException, IOException {
        
        SchemaRequest sr = new SchemaRequest();
        SchemaResponse process2 = sr.process(solrTemplate.getSolrClient());
        logger.debug("{}",process2);
        SchemaRepresentation schemaRepresentation = process2.getSchemaRepresentation();
        logger.debug("{}",schemaRepresentation);
        schemaRepresentation.getFields().forEach(fld -> {logger.debug("{}", fld);});
        
        FacetField facetRequest = new FacetField(fieldName);
//        solrTemplate.getSolrClient().query(params)
//        sourceRepository.
//        String sql = "SELECT \"data\"->'properties'->>'" + fieldName + "', count(id) from source_data group by 1";
//        logger.debug(sql);
//        Query query = getManager().createNativeQuery(sql);
//        Map<String,Long> map = new HashMap<>();
//        List resultList = query.getResultList();
//        logger.debug("{}",resultList);
//        for (Object[] object : (List<Object[]>)(List<?>)resultList) {
//            map.put((String)object[0], ((Number) object[1]).longValue());
//        }
//        logger.debug("{}",map);
        return null;
    }

}
