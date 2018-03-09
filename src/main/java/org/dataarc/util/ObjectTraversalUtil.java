package org.dataarc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

public class ObjectTraversalUtil {

    public static void traverse(Map<String, Object> properties, Map<String, Object> newMap, FieldDataCollector collector) {
        traverseMap(properties, newMap, collector, null, null);
    }

    @SuppressWarnings("unchecked")
    public static void traverseMap(Map<String, Object> properties, Map<String, Object> newMap, final FieldDataCollector collect, final String _parent,
            String parentKey) {
        String __parent = _parent;
        if (__parent == null) {
            __parent = "";
        } else if (StringUtils.isBlank(__parent)) {
            __parent = parentKey;
        } else {
            __parent += "." + parentKey;
        }
        final String parent = __parent;
        properties.keySet().forEach(key -> {
            Object arg2 = properties.get(key);
            String normapName_ = collect.add(parent, key, arg2);
            if (normapName_ != null) {
                String normapName = new String(normapName_);
                if (StringUtils.contains(normapName, ".")) {
                    normapName = StringUtils.substringAfterLast(normapName, ".");
                }
                if (arg2 instanceof Collection) {
                    List<Object> childList = new ArrayList<>();
                    String term = normapName;
                    if (StringUtils.isBlank(term)) {
                        term = StringUtils.substringBeforeLast(normapName_, ".");
                    }
                    if (StringUtils.isBlank(term)) {
                        term = normapName_;
                    }
                    System.out.println(term + "  " + normapName_ + " " + normapName);
                    newMap.put(term, childList);

                    ((Collection<Object>) arg2).forEach(new Consumer<Object>() {

                        @Override
                        public void accept(Object t) {
                            if (t instanceof Map<?, ?>) {
                                Map<String, Object> childMap = new HashMap<>();
                                childList.add(childMap);
                                Map<String, Object> map = (Map<String, Object>) t;
                                traverseMap(map, childMap, collect, parent, key);
                            }
                        }

                    });
                } else if (arg2 instanceof Map) {
                    Map<String, Object> childMap = new HashMap<>();
                    newMap.put(normapName, childMap);
                    traverseMap((Map<String, Object>) arg2, childMap, collect, parent, key);
                } else {
                    if (normapName != null) {
                        newMap.put(normapName, properties.get(key));
                    }
                }
            }
        });

    }
}
