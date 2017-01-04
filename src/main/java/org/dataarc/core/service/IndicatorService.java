package org.dataarc.core.service;

import java.util.List;

import org.dataarc.bean.DataEntry;
import org.dataarc.bean.Indicator;
import org.dataarc.core.dao.ImportDao;
import org.dataarc.core.dao.IndicatorDao;
import org.dataarc.core.dao.QueryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IndicatorService {

    @Autowired
    private IndicatorDao indicatorDao;

    @Autowired
    private QueryDao queryDao;

    @Autowired
    private ImportDao importDao;
    
    @Transactional(readOnly = false)
    public void save(Indicator indicator) {
        indicatorDao.save(indicator);
    }

    @Transactional(readOnly = true)
    public Indicator findById(Long id) {
        return indicatorDao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Indicator> findAllForSchema(String schemaName) {
        return indicatorDao.findAllForSchema(schemaName);
    }

    @Transactional(readOnly=false)
    public Indicator merge(Indicator indicator) {
        return indicatorDao.merge(indicator);
        
    }
    
    @Transactional(readOnly=false)
    public void indexIndicators(String schemaName) throws Exception {
        for (Indicator indicator : findAllForSchema(schemaName)) {
            for (DataEntry entry : queryDao.getMatchingRows(indicator.getQuery())) {
                entry.getIndicators().add(indicator.getName());
                importDao.save(entry);
            };
        }
    }

}
