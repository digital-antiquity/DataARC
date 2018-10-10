package org.dataarc.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.dataarc.bean.TemporalCoverage;
import org.dataarc.bean.schema.Schema;
import org.dataarc.bean.schema.SchemaField;
import org.dataarc.bean.schema.Value;
import org.dataarc.core.dao.SchemaDao;
import org.dataarc.core.dao.TemporalCoverageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TemporalCoverageService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TemporalCoverageDao temporalCoverageDao;
    @Autowired
    private SchemaDao schemaDao;

    @Transactional(readOnly = true)
    public TemporalCoverage find(String term) {
        return temporalCoverageDao.find(term);
    }

    @Transactional(readOnly = true)
    public List<TemporalCoverage> findAll() {
        return temporalCoverageDao.findAll();
    }

    public Object findUserValue() {
        Map<Schema, List<Value>> vals = new HashMap<>();
        for (Schema schema : schemaDao.findAll()) {
            List<Value> _vals = new ArrayList<>();
            for (SchemaField field : schema.getFields()) {
                if (field.isEndField() || field.isStartField() || field.isTextDateField()) {
                    for (Value val : field.getValues()) {
                        // if we're not numeric add
                        if (!val.getValue().matches("^\\-?(\\d|\\.|\\,|\\s)+$")) {
                            _vals.add(val);
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(_vals)) {
                vals.put(schema, _vals);
            }
        }
        return vals;
    }
}
