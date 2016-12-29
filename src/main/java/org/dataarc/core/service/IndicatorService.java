package org.dataarc.core.service;

import java.util.List;

import org.dataarc.bean.Indicator;
import org.dataarc.core.dao.IndicatorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IndicatorService {

    @Autowired
    private IndicatorDao indicatorDao;

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

}
