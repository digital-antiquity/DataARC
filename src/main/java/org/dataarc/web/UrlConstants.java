package org.dataarc.web;

public interface UrlConstants {

    String SCHEMA_LIST_FIELDS = "/api/fields";
    String SCHEMA_LIST = "/api/schema";
    String TOPIC_MAP_VIEW = "/api/topicmap/view";
    String TOPIC_INDICATOR_LIST = "/api/topicmap/indicators";
    String QUERY_DATASTORE = "/api/query/datastore";
    String SAVE_INDICATOR = "/api/indicator/save";
    String UPDATE_INDICATOR = "/api/indicator/{id}";
    String VIEW_INDICATOR = "/api/indicator/{id}";
    String LIST_INDICATORS = "/api/indicator";
    String REINDEX = "/api/admin/reindex";
    String ADMIN = "/admin";
    String ADMIN_SOURCE_DATA = "/admin/source";
    String ADMIN_SOURCE_UPLOAD_FILE = "/admin/uploadFile";
    String ADMIN_TOPIC_IMPORT = "/admin/uploadTopic";
    String ADMIN_TOPIC_DATA = "/admin/topic";
    String ADMIN_TOPIC_UPLOAD_FILE = "/admin/topicUploadFile";

}
