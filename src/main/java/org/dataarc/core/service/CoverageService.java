package org.dataarc.core.service;

import java.util.List;

import org.dataarc.bean.TemporalCoverage;
import org.dataarc.core.dao.CoverageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoverageService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    CoverageDao coverageDao;

    @Transactional(readOnly = false)
    public void save(TemporalCoverage entry) {
        coverageDao.save(entry);
    }

    @Transactional(readOnly = true)
    public List<TemporalCoverage> findAll() {
        return coverageDao.findAll();
    }

}
