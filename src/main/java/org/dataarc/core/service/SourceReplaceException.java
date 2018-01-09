package org.dataarc.core.service;

import java.util.List;

import org.dataarc.bean.Indicator;

public class SourceReplaceException extends Exception {

    private List<Indicator> inds;

    public SourceReplaceException(List<Indicator> inds) {
        this.inds = inds;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 8687315132406261536L;

}
