package org.dataarc.core.service;

import java.util.Date;

import org.dataarc.bean.DataArcUser;
import org.dataarc.core.dao.DataArcUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atlassian.crowd.integration.rest.entity.UserEntity;

@Service
public class UserService {

    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final String EDITOR_ROLE = "ROLE_EDITOR";

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

    @Transactional(readOnly = false)
    public void saveOrUpdateUser(DataArcUser user_, UserEntity userEntity) {
        DataArcUser user = user_;
        if (user == null) {
            user = new DataArcUser();
            user.setDateCreated(new Date());
            user.setExternalId(userEntity.getExternalId());
        }
        user.setEmail(userEntity.getEmailAddress());
        user.setUsername(userEntity.getName());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setLastLogin(new Date());
        save(user);

    }

}
