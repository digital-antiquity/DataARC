package org.dataarc.core.query;

public enum MatchType {
    EQUALS, CONTAINS, DOES_NOT_EQUAL, GREATER_THAN, LESS_THAN;

    public Object mongoName() {
        switch (this) {
            case CONTAINS:
                return "";
            case DOES_NOT_EQUAL:
                return "";
            case EQUALS:
                return "=";
            case GREATER_THAN:
                return ">";
            case LESS_THAN:
                return "<";
            default:
                return "=";
        }
    }
}
