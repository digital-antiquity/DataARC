package org.dataarc.bean.topic;

import java.util.List;

import org.dataarc.bean.AbstractPersistable;

public class Topic extends AbstractPersistable {

    private String name;
    private String identifier;
    private List<String> varients;
    private Association association;
    private Topic parent;
}
