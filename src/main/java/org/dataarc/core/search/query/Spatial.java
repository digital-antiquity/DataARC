package org.dataarc.core.search.query;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class Spatial implements Serializable {

    private static final long serialVersionUID = 3608731620731620810L;
    private double[] topLeft;
    private double[] bottomRight;
    private String region;

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

    public void expandBy(Integer expandBy) {
        double d1 = (topLeft[0] - bottomRight[0]) * (double) expandBy;
        ;
        double d2 = (topLeft[1] - bottomRight[1]) * (double) expandBy;
        ;
        topLeft[0] -= d1;
        bottomRight[0] += d1;
        topLeft[1] -= d2;
        bottomRight[1] += d2;

    }

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
