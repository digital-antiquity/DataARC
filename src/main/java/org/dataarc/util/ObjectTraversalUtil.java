package org.dataarc.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

public class ObjectTraversalUtil {

    public static void traverse(Map<String, Object> properties, FieldDataCollector collector) {
        traverseMap(properties, collector, null, null);
    }

    public static void traverseMap(Map<String, Object> properties, final FieldDataCollector collect, final String _parent, String parentKey) {
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
            collect.add(parent, key, arg2);
            if (arg2 instanceof Collection) {
                ((Collection) arg2).forEach(new Consumer() {

                    @Override
                    public void accept(Object t) {
                        if (t instanceof Map<?, ?>) {
                            Map<String, Object> map = (Map<String, Object>) t;
                            traverseMap(map, collect, parent, key);
                        }
                    }

                });
            }
            if (arg2 instanceof Map) {
                traverseMap((Map<String, Object>) arg2, collect, parent,key);
            }
        });

    }
}
