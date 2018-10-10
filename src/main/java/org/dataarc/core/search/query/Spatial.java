package org.dataarc.core.search.query;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Representation of a Spatial Query Part (SOLR)
 * 
 * @author abrin
 *
 */
public class Spatial implements Serializable {

    private static final long serialVersionUID = 3608731620731620810L;
    private double[] topLeft;
    private double[] bottomRight;
    private String region;
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    public Spatial() {
    }

    public Spatial(double[] i, double[] j) {
        this.topLeft = i;
        this.bottomRight = j;
    }

    public double[] getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(double[] bottomRight) {
        this.bottomRight = bottomRight;
    }

    public double[] getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(double[] topLeft) {
        this.topLeft = topLeft;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Spatial expandBy(Integer expandBy) {
        double multi = (double) expandBy * 0.1;
        // silly, but the 'deep clone nulls the logger)
        if (logger != null) {
            logger.debug("expand from: [{}-{}]", topLeft, bottomRight);
        }

        if (topLeft == null || bottomRight == null || topLeft.length < 2 || bottomRight.length < 2) {
            return this;
        }
        double d1 = Math.abs(topLeft[0] - bottomRight[0]) * multi / 2;
        double d2 = Math.abs(topLeft[1] - bottomRight[1]) * multi / 2;
        // silly, but the 'deep clone nulls the logger)
        if (logger != null) {
            logger.debug(d1 + " - " + d2 + " :" + multi);
        }
        if (topLeft[0] > 0) {
            topLeft[0] += d1;
        } else {
            topLeft[0] -= d1;
        }

        if (bottomRight[0] < 0) {
            bottomRight[0] += d1;
        } else {
            bottomRight[0] += d1;
        }

        if (topLeft[1] > 0) {
            topLeft[1] += d2;
        } else {
            topLeft[1] -= d2;
        }

        if (bottomRight[1] < 0) {
            bottomRight[1] += d2;
        } else {
            bottomRight[1] -= d2;
        }

        if (topLeft[0] > 180) {
            topLeft[0] = 180;
        }
        if (topLeft[1] > 90) {
            topLeft[1] = 90;
        }

        if (bottomRight[0] < -180) {
            bottomRight[0] = -180;
        }
        if (bottomRight[1] < -90) {
            bottomRight[1] = -90;
        }

        if (topLeft[0] < -180) {
            topLeft[0] = -180;
        }
        if (topLeft[1] < -90) {
            topLeft[1] = -90;
        }

        if (bottomRight[0] > 180) {
            bottomRight[0] = 180;
        }
        if (bottomRight[1] > 90) {
            bottomRight[1] = 90;
        }

        if (logger != null) {
            logger.debug("expand   to: [{}-{}]", topLeft, bottomRight);
        }
        return this;
    }

    @JsonIgnore
    public boolean isEmpty() {
        if (StringUtils.isNotBlank(getRegion())) {
            return false;
        }

        if (getBottomRight() == null ||
                getBottomRight().length < 2) {
            return true;
        }

        if (getTopLeft() == null ||
                getTopLeft().length < 2) {
            return true;
        }

        return false;
    }
}
