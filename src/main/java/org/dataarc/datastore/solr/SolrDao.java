package org.dataarc.datastore.solr;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.response.schema.SchemaRepresentation;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.common.util.ContentStreamBase;
import org.dataarc.bean.DataEntry;
import org.dataarc.core.dao.ImportDao;
import org.geojson.Feature;
import org.geojson.GeoJsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTWriter;

@Component
public class SolrDao implements ImportDao {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Autowired
    SolrTemplate template;

    @Autowired
    SourceRepository sourceRepository;
    
    public Map<String, String> getSchema() throws SolrServerException, IOException {
        SchemaRequest sr = new SchemaRequest();
        SchemaResponse process2 = sr.process(template.getSolrClient());
        logger.debug("{}", process2);
        SchemaRepresentation schemaRepresentation = process2.getSchemaRepresentation();
        logger.debug("{}", schemaRepresentation);
        Map<String,String> fields = new HashMap<>();
        schemaRepresentation.getFields().forEach(fld -> {
            fields.put((String)fld.get("name"), (String)fld.get("type"));
            logger.debug("{} ({})", fld.get("name"), fld.get("type"));
        });
        return fields;
    }

    public Map<String, Long> getDistinctValues(String source, String fieldName) throws SolrServerException, IOException {
        Map<String, Long> out = new HashMap<>();
        Criteria criteria = new Criteria("source").is(source);
        FacetQuery fq = new SimpleFacetQuery(criteria);
        fq.setFacetOptions(new FacetOptions(fieldName));
        FacetPage<DataEntry> page = template.queryForFacetPage(fq, DataEntry.class);
        logger.debug("{}", page);
        page.getFacetResultPage(fieldName).getContent().forEach(field -> {
            out.put(field.getValue(), field.getValueCount());
        });
        logger.debug("{}", out);
        return out;
    }

    @Override
    public void deleteAll() {
        sourceRepository.deleteAll();        
    }

    @Override
    public void load(Feature feature, Map<String, Object> properties) throws Exception {
        
        String json = new ObjectMapper().writeValueAsString(properties);
        ContentStreamUpdateRequest request = new ContentStreamUpdateRequest("/update/json/docs");
        request.setParam("json.command", "false");
        request.setParam("split", "/");

        request.addContentStream(new ContentStreamBase.StringStream(json));
        UpdateResponse response = request.process(template.getSolrClient());
        template.getSolrClient().commit();
        
    }

    @Override
    public void enhanceProperties(Feature feature, Map<String, Object> properties) {
        GeoJsonObject geometry = feature.getGeometry();
        if (geometry instanceof org.geojson.Point) {
            org.geojson.Point point_ = (org.geojson.Point) geometry;
            if (point_.getCoordinates() != null) {
                double latitude = point_.getCoordinates().getLatitude();
                double longitude = point_.getCoordinates().getLongitude();
                com.vividsolutions.jts.geom.Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
                WKTWriter wrt = new WKTWriter();
                String str = wrt.write(point);
                properties.put("coordiates", str);
                properties.put("coords", String.format("%s, %s", longitude, latitude));
                properties.put("c_p", String.format("%s, %s", longitude, latitude));
                // properties.put("point", point);
            }
        }
        
    }

    @Override
    public Iterable<DataEntry> findAll() {
        return sourceRepository.findAll();
    }
}
