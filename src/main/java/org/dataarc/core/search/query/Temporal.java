package org.dataarc.core.search.query;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Temporal implements Serializable {

    private static final long serialVersionUID = 8871743799330705618L;
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

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
        if (end != null && start != null) {
            if (logger != null) {
                logger.debug("expanding from: {} - {}", start, end);
            }
            int d = Math.abs(end - start);
            start -= d * (expandBy -1);
            end += d * (expandBy -1);
            if (logger != null) {
                logger.debug("expanding   to: {} - {}", start, end);
            }
        }

    }

    @JsonIgnore
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
