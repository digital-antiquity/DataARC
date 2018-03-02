package org.dataarc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

public class ObjectTraversalUtil {

    public static void traverse(Map<String, Object> properties, Map<String,Object> newMap, FieldDataCollector collector) {
        traverseMap(properties, newMap, collector, null, null);
    }

    public static void traverseMap(Map<String, Object> properties,Map<String,Object> newMap, final FieldDataCollector collect, final String _parent, String parentKey) {
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
            String normapName = collect.add(parent, key, arg2);
            if (arg2 instanceof Collection) {
                List<Object> childList = new ArrayList<>();
                newMap.put(normapName, childList);

                ((Collection) arg2).forEach(new Consumer() {

                    @Override
                    public void accept(Object t) {
                        if (t instanceof Map<?, ?>) {
                            Map<String,Object> childMap = new HashMap<>();
                            childList.add(childMap);
                            Map<String, Object> map = (Map<String, Object>) t;
                            traverseMap(map, childMap, collect, parent, key);
                        }
                    }

                });
            } else if (arg2 instanceof Map) {
                Map<String,Object> childMap = new HashMap<>();
                newMap.put(normapName, childMap);
                traverseMap((Map<String, Object>) arg2, childMap, collect, parent, key);
            } else {
                if (normapName != null) {
                    newMap.put(normapName, properties.get(key));
                }
            }
        });

    }
}
