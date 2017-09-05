package org.dataarc.core.search.query;

import java.io.Serializable;

public class Temporal implements Serializable {

    private static final long serialVersionUID = 8871743799330705618L;

    private Integer start;
    private Integer end;

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
