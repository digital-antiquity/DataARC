package org.dataarc.core.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.dataarc.web.api.SearchResultObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrFacetBuilder {

    private static final String VAL = "val";
    private static final String MISSING = "missing";
    private static final String BUCKETS = "buckets";
    private static final String COUNT = "count";
    private static final boolean includeMissing = false;
    private static final String FACETS = "facets";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Set<String> subgroups = new HashSet<>();

    public SolrFacetBuilder(List<String> subgroups2) {
        this.subgroups.addAll(subgroups2);
    }

    public void buildResultsFacets(SearchResultObject result, QueryResponse query) {
        @SuppressWarnings("rawtypes")
        SimpleOrderedMap facetMap = (SimpleOrderedMap) query.getResponse().get(FACETS);

        logger.debug("{}", facetMap);
        for (String field : (Set<String>) facetMap.asShallowMap().keySet()) {
            if (logger.isTraceEnabled()) {
                logger.trace("{}", field);
                logger.trace("{} : {}", facetMap.get(field).getClass(), facetMap.get(field));
            }
            if (facetMap.get(field) instanceof SimpleOrderedMap) {
                SimpleOrderedMap<?> object = (SimpleOrderedMap<?>) facetMap.get(field);
                if (object == null || object.get(BUCKETS) == null) {
                    continue;
                }
                Map<String, Object> map = appendChildren(object);
                result.getFacets().put(field, map);
            }
        }

        if (CollectionUtils.isNotEmpty(query.getFacetFields())) {
            for (FacetField facet : query.getFacetFields()) {
                Map<String, Object> map = new HashMap<>();
                facet.getValues().forEach(val -> {
                    map.put(val.getName(), val.getCount());
                });
                result.getFacets().put(facet.getName(), map);
            }
        }
    }

    private HashMap<String, SimpleOrderedMap<?>> getSubgroups(SimpleOrderedMap<?> f) {
        HashMap<String, SimpleOrderedMap<?>> ret = new HashMap<>();
        for (String subgroup : subgroups) {
            Object sub = f.get(subgroup);
            if (sub != null) {
                ret.put(subgroup, (SimpleOrderedMap<?>) sub);
            }
        }
        return ret;
    }

    public Map<String, Object> appendChildren(SimpleOrderedMap<?> object) {
        Map<String, Object> map = new HashMap<>();
        List<?> list = (List<?>) object.get(BUCKETS);

        for (Object obj : list) {
            SimpleOrderedMap<?> f = (SimpleOrderedMap<?>) obj;
            map.put(f.get(VAL).toString(), ((Number) f.get(COUNT)).longValue());
            HashMap<String, SimpleOrderedMap<?>> subgroups = getSubgroups(f);
            if (!subgroups.isEmpty()) {
                Map<String, Object> subMap = new HashMap<>();
                map.put(f.get(VAL).toString(), subMap);
                subMap.put(COUNT, ((Number) f.get(COUNT)).longValue());

                for (String key : subgroups.keySet()) {
                    subMap.put(key, appendChildren((SimpleOrderedMap<?>) subgroups.get(key)));
                }
            }
            for (String subgroup : this.subgroups) {
                Object sub = f.get(subgroup);
                if (sub != null) {
                }
            }
        }
        if (includeMissing) {
            Object miss = object.get(MISSING);
            if (miss != null) {
                SimpleOrderedMap<?> missing = (SimpleOrderedMap<?>) miss;
                map.put("", ((Number) missing.get(COUNT)).longValue());
            }
        }
        return map;
    }

}
