package org.dataarc.core.service;

import java.util.List;

import org.dataarc.bean.DataArcUser;
import org.dataarc.bean.SavedSearch;
import org.dataarc.core.dao.SavedSearchDao;
import org.dataarc.core.search.query.SearchQueryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SavedSearchService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SavedSearchDao searchDao;

    @Transactional(readOnly = false)
    public SavedSearch saveSearch(String title, SearchQueryObject query_, DataArcUser user) {
        SavedSearch savedSearch = new SavedSearch(title, query_, user);
        searchDao.save(savedSearch);
        return savedSearch;
    }

    @Transactional(readOnly=true)
    public SavedSearch find(Long id) {
        return searchDao.findById(id);
    }

    public List<SavedSearch> findAll() {
        return searchDao.findAll();
    }


}
