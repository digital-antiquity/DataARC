package org.dataarc.core.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.ContentStreamBase;
import org.dataarc.core.dao.QueryDao;
import org.dataarc.core.query.solr.SourceRepository;
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

        try {
            queryDao.getDistinctValues("source");
        } catch (SolrServerException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void getQueryResponse(AbstractUpdateRequest request)
            throws org.apache.solr.client.solrj.SolrServerException, IOException {
        UpdateResponse response = request.process(template.getSolrClient());

        template.getSolrClient().commit();

    }
}
