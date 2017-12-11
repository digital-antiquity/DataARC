package org.dataarc.bean;

public enum ActionType {

    SAVE, DELETE, UPDATE;

    public String getLabel() {
        switch (this) {
            case SAVE:
                return "added";
            case UPDATE:
                return "updated";
            case DELETE:
                return "deleted";
            default:
                break;
        }
        return "";
    }
}
