package org.dataarc.core.service;

import org.dataarc.bean.DataArcUser;
import org.dataarc.core.dao.DataArcUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    DataArcUserDao userDao;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional(readOnly = true)
    public DataArcUser findByExternalId(String userId) {
        return userDao.findByUserId(userId);
    }

    @Transactional(readOnly = false)
    public void save(DataArcUser user) {
        userDao.save(user);
    }

}
