package org.dataarc.core.search.query;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class Temporal implements Serializable {

    private static final long serialVersionUID = 8871743799330705618L;

    private Integer start;
    private Integer end;
    private String period;

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

    public void expandBy(Integer expandBy) {
        int d = end - start;
        start -= d*expandBy;
        end += d*expandBy;
        
    }

    public boolean isEmpty() {
        if (getEnd() == getStart() && getStart() == null && StringUtils.isBlank(period)) {
            return true;
        }
        return false;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

}
