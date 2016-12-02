package org.dataarc.core.service;

import org.dataarc.core.dao.QueryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryService {

    @Autowired
    private QueryDao queryDao;
}
