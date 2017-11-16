package org.dataarc.core.service;


import java.util.List;

import org.dataarc.bean.TemporalCoverage;
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
        
        @Transactional(readOnly=true)
        public TemporalCoverage find(String term) {
            return temporalCoverageDao.find(term);
        }

        @Transactional(readOnly=true)
        public List<TemporalCoverage> findAll() {
            return temporalCoverageDao.findAll();
        }
}
