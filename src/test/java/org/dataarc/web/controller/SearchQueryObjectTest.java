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
}
