package org.dataarc.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.dataarc.bean.DataEntry;
import org.dataarc.core.query.FilterQuery;
import org.dataarc.core.query.Operator;
import org.dataarc.core.query.QueryPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
//@Transactional
public class MongoQueryDao  {
}
