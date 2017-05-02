package org.dataarc.core.search;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.dataarc.core.legacy.search.IndexFields;
import org.dataarc.core.service.SolrIndexObject;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Box;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.Query.Operator;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * used for searching the Lucene index.
 * 
 * @author abrin
 *
 */
@Service
public class SearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    private final Logger logger = Logger.getLogger(getClass());

    static int limit = 1_000_000;

    /**
     * Perform a search passing in the bounding box and search terms
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param start
     * @param end
     * @param list
     * @param term
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public FeatureCollection search(SearchQueryObject sqo)
            throws IOException  {
        // Rectangle rectangle = ctx.makeRectangle(Math.min(x1, x2), Math.max(x1, x2), Math.min(y1, y2), Math.max(y1, y2));
        FeatureCollection fc = new FeatureCollection();
        // SpatialArgs args = new SpatialArgs(SpatialOperation.IsWithin, rectangle);
        // Filter filter = strategy.makeFilter(args);
        Criteria temporalConditions = null;
        FacetQuery query = new SimpleFacetQuery(new Criteria(Criteria.WILDCARD).expression(Criteria.WILDCARD));
        if (sqo.getEnd() != null) {
            temporalConditions = new Criteria(IndexFields.END).greaterThanEqual(sqo.getStart());
        }
        if (sqo.getStart() != null) {
            Criteria start = new Criteria(IndexFields.START).lessThanEqual(sqo.getEnd());
            if (temporalConditions == null) {
                temporalConditions = start;
            } else {
                temporalConditions = temporalConditions.and(start);
            }
        }

        if (temporalConditions != null) {
            query = query.addCriteria(temporalConditions);
        }
        if (sqo.getTopLeft() != null && sqo.getBottomRight() != null) {
            query.addCriteria(new Criteria(IndexFields.POINT).near(new Box(sqo.getTopLeft(), sqo.getBottomRight())));
        }

        if (!CollectionUtils.isEmpty(sqo.getKeywords())) {
            query.addCriteria(new Criteria(IndexFields.KEYWORD).in(sqo.getKeywords()));
        }

        if (!CollectionUtils.isEmpty(sqo.getTopicIds())) {
            query.addCriteria(new Criteria(IndexFields.TOPIC_ID).in(sqo.getTopicIds()));
        }

        if (!CollectionUtils.isEmpty(sqo.getSources())) {
            
            Criteria in = null;
            for (String source : sqo.getSources()) {
                Criteria c = Criteria.where("source_txt_en").is(source);
                if (in == null) {
                    in = c;
                } else {
                    in = in.or(c);
                }
            };
            if (in != null) {
                query.addCriteria(in);
            }
        }

        // query.addProjectionOnField("*");
        // query.addProjectionOnField("distance:geodist()");
        query.setDefaultOperator(Operator.AND);
        query.setRows(limit);
        //
        // final SolrQuery solrQuery = qp.constructSolrQuery(query);
        // solrQuery.add("sfield", "store");
        // solrQuery.add("pt", GeoConverters.GeoLocationToStringConverter.INSTANCE.convert(new GeoLocation(45.15, -93.85)));
        // solrQuery.add("d", GeoConverters.DistanceToStringConverter.INSTANCE.convert(new Distance(5)));
        //
        // List<EventDocument> result = template.execute(new SolrCallback<List<EventDocument>>() {
        //
        // @Override
        // public List<EventDocument> doInSolr(SolrServer solrServer) throws SolrServerException, IOException {
        // return template.getConverter().read(solrServer.query(solrQuery).getResults(), EventDocument.class);
        // }
        // });

        FacetPage<SolrIndexObject> facetPage = solrTemplate.queryForFacetPage(query, SolrIndexObject.class);
        logger.debug(String.format("results: %s page:%s size: %s", facetPage.getTotalElements(), facetPage.getNumber(), facetPage.getSize()));

        facetPage.getContent().forEach(obj -> {
            try {
                // create a point for each result
                if (obj.getPosition() == null || obj.getPosition() == null) {
                } else {
                    fc.add(obj.copyToFeature());
                }
            } catch (Throwable t) {
                logger.warn(t);
            }
        });

        return fc;
    }


}
