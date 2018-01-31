package org.dataarc.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "temporal_coverage")
public class TemporalCoverage extends AbstractPersistable {

    private static final long serialVersionUID = 4212821207919813848L;

    @Column(name = "start_date")
    private Integer startDate;
    @Column(name = "end_date")
    private Integer endDate;
    @Column(length = 1024)
    private String term;
    @Column(length = 1024)
    private String description;

    public Integer getStartDate() {
        return startDate;
    }

    public void setStartDate(Integer startDate) {
        this.startDate = startDate;
    }

    public Integer getEndDate() {
        return endDate;
    }

    public void setEndDate(Integer endDate) {
        this.endDate = endDate;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
