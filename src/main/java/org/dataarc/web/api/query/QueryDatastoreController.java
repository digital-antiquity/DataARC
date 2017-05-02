package org.dataarc.web.api.query;

import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dataarc.bean.DataEntry;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.service.QueryService;
import org.dataarc.web.api.AbstractRestController;
import org.dataarc.web.api.schema.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueryDatastoreController extends AbstractRestController {

    @Autowired
    private QueryService queryService;

    @RequestMapping(path = UrlConstants.QUERY_DATASTORE, method = RequestMethod.POST)
    public Iterable<DataEntry> queryDatastore(@RequestBody(required = true) FilterQuery fq) throws Exception {
        logger.debug("Query: {} ", fq);
        if (fq == null) {
            return new ArrayList<DataEntry>();
        }

        if (CollectionUtils.isEmpty(fq.getConditions())) {
            return new ArrayList<DataEntry>();
        }
        
        //default query for blank from UI
        if (fq.getConditions().size() == 1 &&  fq.getConditions().get(0).getType() == null && StringUtils.isBlank(fq.getConditions().get(0).getFieldName())) {
            return new ArrayList<DataEntry>();
            
        }

        try {
            return queryService.getMatchingRows(fq);
        } catch (Exception e) {
            logger.error("{}", e, e);
            throw e;
        }

    }
}
