package org.dataarc.core.service;

import java.util.Date;
import java.util.List;

import org.dataarc.bean.ActionType;
import org.dataarc.bean.ChangeLogEntry;
import org.dataarc.bean.DataArcUser;
import org.dataarc.bean.ObjectType;
import org.dataarc.core.dao.ChangeLogDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeLogService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    ChangeLogDao changeLogDao;

    @Transactional(readOnly = false)
    public void save(ChangeLogEntry entry) {
        changeLogDao.save(entry);
    }

    @Transactional(readOnly = true)
    public List<ChangeLogEntry> findAll() {
        return changeLogDao.findAll();
    }

    @Transactional(readOnly = false)
    public void save(ActionType save, ObjectType topic, DataArcUser user, String string) {
        ChangeLogEntry change = new ChangeLogEntry( save, topic, user, string);
        change.setDateCreated(new Date());
        save(change);
        
    }
}
