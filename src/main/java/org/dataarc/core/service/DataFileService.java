package org.dataarc.core.service;

import java.util.List;

import org.dataarc.bean.file.DataFile;
import org.dataarc.core.dao.file.DataFileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataFileService {

    @Autowired
    private DataFileDao dataFileDao;
    
    @Transactional(readOnly=true)
    public List<DataFile> findBySchemaId(Long id) {
        return dataFileDao.findBySchemaId(id);
    }
}   
