package org.dataarc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

/**
 * A tool to walk through an object and it's child properties and useful to collect unique field info and paths
 * @author abrin
 *
 */
public class ObjectTraversalUtil {

    public static void traverse(Map<String, Object> properties, Map<String, Object> newMap, FieldDataCollector collector) {
        traverseMap(properties, newMap, collector, null, null);
    }

    @SuppressWarnings("unchecked")
    
    /**
     * Traverse a field map
     * @param properties
     * @param newMap
     * @param collect
     * @param _parent
     * @param parentKey
     */
    public static void traverseMap(Map<String, Object> properties, Map<String, Object> newMap, final FieldDataCollector collect, final String _parent,
            String parentKey) {
        // identify the parent so we can use it in building out the field path
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
            // get the normalized value name from the field data collector
            String normapName_ = collect.add(parent, key, arg2);
            if (normapName_ != null) {
                // if we've seen it before, get the name
                String normapName = new String(normapName_);
                if (StringUtils.contains(normapName, ".")) {
                    normapName = StringUtils.substringAfterLast(normapName, ".");
                }
                // if we have children, then we need to traverse them
                
                if (arg2 instanceof Collection) {
                    List<Object> childList = new ArrayList<>();
                    String term = normapName;
                    if (StringUtils.isBlank(term)) {
                        term = StringUtils.substringBeforeLast(normapName_, ".");
                    }
                    if (StringUtils.isBlank(term)) {
                        term = normapName_;
                    }
                    
                    // traverse children with new definition of parent
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
