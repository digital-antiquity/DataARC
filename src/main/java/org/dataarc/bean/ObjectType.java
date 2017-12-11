package org.dataarc.bean;

public enum ObjectType {
    DATA_SOURCE, COMBINATOR, TOPIC, GEOJSON;

    public String getLabel() {
        switch (this) {
            case COMBINATOR:
                return "Combinator";
            case DATA_SOURCE:
                return "Data Source";
            case GEOJSON:
                return "GeoJSON File";
            case TOPIC:
                return "Concept Map";
            default:
                break;
        }
        return "";
    }
}
