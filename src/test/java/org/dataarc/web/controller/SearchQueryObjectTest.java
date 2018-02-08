package org.dataarc.web.controller;

import static org.junit.Assert.*;

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
        logger.debug("{} - {}", sqo.getSpatial().getTopLeft(), sqo.getSpatial().getBottomRight());
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
        Spatial spatial = new Spatial(new double[] { -19.40, 66.16 }, new double[] { 23.14, 64.63 });
        double d1 = Math.abs(spatial.getTopLeft()[0] - spatial.getBottomRight()[0]);
        double d2 = Math.abs(spatial.getTopLeft()[1] - spatial.getBottomRight()[1]);
        logger.debug("{} - {}", spatial.getTopLeft(), spatial.getBottomRight());
        Spatial expandBy = spatial.expandBy(2);
        double d1_ = Math.abs(expandBy.getTopLeft()[0] - expandBy.getBottomRight()[0]);
        double d2_ = Math.abs(expandBy.getTopLeft()[1] - expandBy.getBottomRight()[1]);
        logger.debug("{} - {}", expandBy.getTopLeft(), expandBy.getBottomRight());
        logger.debug("d1: {} d1_: {}", d1, d1_);
        logger.debug("d2: {} d2_: {}", d2, d2_);
        assertTrue(d1 < d1_);
        assertTrue(d2 < d2_);
    }

    @Test
    public void testSpatial3() {
        Spatial spatial = new Spatial(new double[] { -23.14, 66.16 }, new double[] { -19.40, 64.63 });
        double d1 = Math.abs(spatial.getTopLeft()[0] - spatial.getBottomRight()[0]);
        double d2 = Math.abs(spatial.getTopLeft()[1] - spatial.getBottomRight()[1]);
        logger.debug("{} - {}", spatial.getTopLeft(), spatial.getBottomRight());
        Spatial expandBy = spatial.expandBy(2);
        logger.debug("{} - {}", expandBy.getTopLeft(), expandBy.getBottomRight());
        double d1_ = Math.abs(spatial.getTopLeft()[0] - spatial.getBottomRight()[0]);
        double d2_ = Math.abs(spatial.getTopLeft()[1] - spatial.getBottomRight()[1]);
        logger.debug("d1: {} d1_: {}", d1, d1_);
        logger.debug("d2: {} d2_: {}", d2, d2_);
        assertTrue(d1 < d1_);
        assertTrue(d2 < d2_);

    }

    @Test
    public void testSpatial4() {
        Spatial spatial = new Spatial(new double[] { -22.1, 65.41 }, new double[] { -19.38, 64.65 });
        double d1 = Math.abs(spatial.getTopLeft()[0] - spatial.getBottomRight()[0]);
        double d2 = Math.abs(spatial.getTopLeft()[1] - spatial.getBottomRight()[1]);
        logger.debug("{} - {}", spatial.getTopLeft(), spatial.getBottomRight());
        Spatial expandBy = spatial.expandBy(2);
        logger.debug("{} - {}", expandBy.getTopLeft(), expandBy.getBottomRight());
        double d1_ = Math.abs(spatial.getTopLeft()[0] - spatial.getBottomRight()[0]);
        double d2_ = Math.abs(spatial.getTopLeft()[1] - spatial.getBottomRight()[1]);
        logger.debug("d1: {} d1_: {}", d1, d1_);
        logger.debug("d2: {} d2_: {}", d2, d2_);
        assertTrue(d1 < d1_);
        assertTrue(d2 < d2_);

    }

    @Test
    public void testSpatial5() {
        Spatial spatial = new Spatial(new double[] { -22.1, 65.41 }, new double[] { -19.38, 64.65 });
        Spatial spatial2 = new Spatial(new double[] { -22.1, 65.41 }, new double[] { -19.38, 64.65 });
        double d1 = Math.abs(spatial.getTopLeft()[0] - spatial.getBottomRight()[0]);
        double d2 = Math.abs(spatial.getTopLeft()[1] - spatial.getBottomRight()[1]);
        logger.debug("{} - {}", spatial.getTopLeft(), spatial.getBottomRight());
        Spatial expandBy = spatial.expandBy(2);
        Spatial expandBy2 = spatial2.expandBy(3);
        logger.debug("{} - {}", expandBy.getTopLeft(), expandBy.getBottomRight());
        double d1_ = Math.abs(spatial.getTopLeft()[0] - spatial.getBottomRight()[0]);
        double d2_ = Math.abs(spatial.getTopLeft()[1] - spatial.getBottomRight()[1]);
        double d1__ = Math.abs(spatial2.getTopLeft()[0] - spatial2.getBottomRight()[0]);
        double d2__ = Math.abs(spatial2.getTopLeft()[1] - spatial2.getBottomRight()[1]);
        logger.debug("d1: {} d1_: {} d1__: {}", d1, d1_, d1__);
        logger.debug("d2: {} d2_: {} d2__: {}", d2, d2_, d2__);
        assertTrue(d1 < d1_);
        assertTrue(d2 < d2_);
        assertTrue(d1_ < d1__);
        assertTrue(d2_ < d2__);

    }

}
