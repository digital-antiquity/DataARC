package org.dataarc.core.search.query;

import java.io.Serializable;

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
}
