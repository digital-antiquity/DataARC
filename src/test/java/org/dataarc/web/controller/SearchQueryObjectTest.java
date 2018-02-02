package org.dataarc.web.controller;

import org.dataarc.core.search.query.SearchQueryObject;
import org.dataarc.core.search.query.Spatial;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchQueryObjectTest {
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());


    @Test
    public void testExpand() {
        SearchQueryObject sqo = new SearchQueryObject();
        sqo.setSpatial(new Spatial(new double[] { -75, 85 }, new double[] { -0.1, 58 }));
        sqo.setIdOnly(false);
        sqo.setExpandBy(2);
        sqo.expand();
        logger.debug("{} - {}", sqo.getSpatial().getTopLeft(), sqo.getSpatial().getBottomRight() );
    }

    @Test
    public void testSpatial() {
        Spatial spatial = new Spatial(new double[] { -75, 85 }, new double[] { -0.1, 58 });
        logger.debug("{} - {}", spatial.getTopLeft(), spatial.getBottomRight());
        Spatial expandBy = spatial.expandBy(2);
        logger.debug("{} - {}", expandBy.getTopLeft(), expandBy.getBottomRight());
    }
    
    @Test
    public void testSpatial2() {
        Spatial spatial = new Spatial(new double[] { 23.14,64.63 }, new double[] {-19.40,66.16 });
        logger.debug("{} - {}", spatial.getTopLeft(), spatial.getBottomRight());
        Spatial expandBy = spatial.expandBy(2);
        logger.debug("{} - {}", expandBy.getTopLeft(), expandBy.getBottomRight());
    }
}
