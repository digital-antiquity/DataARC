package org.dataarc.core.service;

import java.util.List;

import org.dataarc.bean.Combinator;

public class SourceReplaceException extends Exception {

    private List<Combinator> inds;

    public SourceReplaceException(List<Combinator> inds) {
        this.inds = inds;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 8687315132406261536L;

}
