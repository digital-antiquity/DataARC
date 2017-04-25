package org.dataarc.core.search;

public class SearchQueryObject {

    private double[] topLeft;
    private double[] bottomRight;
    private Integer start;
    private Integer end;
    
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
    public Integer getEnd() {
        return end;
    }
    public void setEnd(Integer end) {
        this.end = end;
    }
    public Integer getStart() {
        return start;
    }
    public void setStart(Integer start) {
        this.start = start;
    }
}
