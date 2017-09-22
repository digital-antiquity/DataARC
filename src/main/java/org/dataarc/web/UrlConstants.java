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
    String ADMIN = "/a/admin";
    String ADMIN_SOURCE_DATA = "/a/admin/source";
    String ADMIN_GEOJSON_DATA = "/a/admin/geojson";
    String ADMIN_SOURCE_UPLOAD_FILE = "/a/admin/uploadSourceFile";
    String ADMIN_GEOJSON_UPLOAD_FILE = "/a/admin/uploadGeoJsonFile";
    String ADMIN_TOPIC_IMPORT = "/a/admin/uploadTopic";
    String ADMIN_TOPIC_DATA = "/a/admin/topic";
    String ADMIN_TOPIC_UPLOAD_FILE = "/a/admin/topicUploadFile";
    String SEARCH = "/api/search";
    String ADMIN_MAKE_EDITOR = "/api/admin/makeEditor";
    String ADMIN_MAKE_ADMIN = "/api/admin/makeAdmin";
    String ADMIN_LIST_USERS = "/api/admin/listUsers";
    String UPDATE_FIELD_DISPLAY_NAME = "/a/fields/updateDisplayName";
    String LIST_SCHEMA = "/a/schema";
    String VIEW_SCHEMA = "/a/schema/{name}";
    String DELETE_SCHEMA = "/a/schema/delete/{name}";
    String REGEOCODE = "/apiadmin/regoeocode";
    String LIST_JSON = "/a/geojson";
    String GEOJSON_VIEW = "/geojson/{id}";
    String DELETE_GEOJSON = "/geojson/{id}";
    String RESET_EVERYTHING = "/a/admin/resetData";
    String SCHEMA_TEMPLATES = "/a/admin/schema/template/{name}";
    String LIST_USERS = "/a/users/list";
    String VIEW_USERS = "/a/users/{id}";
    String DELETE_USER = "/a/users/{id}";

}
