package org.dataarc.core.service;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.ObjectUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.util.ContentStreamBase;
import org.dataarc.core.dao.QueryDao;
import org.dataarc.core.query.solr.SourceRepository;
import org.dataarc.util.FieldDataCollector;
import org.dataarc.util.ObjectTraversalUtil;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTWriter;

@Service
// @Transactional
public class ImportService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SourceRepository sourceRepository;

    @Autowired
    SolrTemplate template;

    @Autowired
    QueryDao queryDao;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    Map<String,FieldDataCollector> collectors = new HashMap<>();
    
    // @Transactional(readOnly = false)
    public void loadData(String filename) {
        sourceRepository.deleteAll();
        try {
            FeatureCollection featureCollection = new ObjectMapper().readValue(new FileInputStream(filename), FeatureCollection.class);
            for (Iterator<Feature> iterator = featureCollection.getFeatures().iterator(); iterator.hasNext();) {
                Feature feature = iterator.next();
                // logger.debug("feature: {}", feature);
                // String source = (String) feature.getProperties().get("source");
                Map<String, Object> properties = feature.getProperties();
                String schema = (String)properties.get("source");
                collectors.putIfAbsent(schema, new FieldDataCollector(schema));
                FieldDataCollector collector = collectors.get(schema);
                ObjectTraversalUtil.traverse(properties, collector);
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

                String json = new ObjectMapper().writeValueAsString(properties);
                if (json.contains("properties")) {
                    logger.debug(">>>> " + json);
                }
                ContentStreamUpdateRequest request = new ContentStreamUpdateRequest("/update/json/docs");
                request.setParam("json.command", "false");
                request.setParam("split", "/");

                request.addContentStream(new ContentStreamBase.StringStream(json));
                getQueryResponse(request);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SolrServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (FieldDataCollector collector : collectors.values()) {
            String name = collector.getSchemaName();
            collector.getFields().forEach(field -> {
                logger.debug("{} {} ({})", name, field, collector.getType(field));
                
            });
        }
    }

    private void getQueryResponse(AbstractUpdateRequest request)
            throws org.apache.solr.client.solrj.SolrServerException, IOException {
        UpdateResponse response = request.process(template.getSolrClient());

        template.getSolrClient().commit();

    }
}
