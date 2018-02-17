package org.dataarc.core.dao;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.dataarc.bean.topic.CategoryAssociation;
import org.dataarc.bean.topic.Topic;
import org.dataarc.bean.topic.TopicMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TopicDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager manager;

    public void save(Topic topic) {
        manager.persist(topic);
    }

    public List<Topic> findTopicsForIndicators() {
        TypedQuery<Topic> query = manager.createQuery(
                "from Topic t where t in (select fr from Association asoc inner join asoc.from as fr) or t in (select fr from Association asoc inner join asoc.to as fr)",
                Topic.class);
        return query.getResultList();
    }

    public Topic findTopicByIdentifier(String topic) {
        TypedQuery<Topic> query = manager.createQuery("from Topic t where t.identifier =:identifier", Topic.class);
        query.setParameter("identifier", topic);
        query.setMaxResults(1);
        return query.getSingleResult();
    }

    public Topic findById(Long id) {
        TypedQuery<Topic> query = manager.createQuery("from Topic t where t.id =:id", Topic.class);
        query.setParameter("id", id);
        query.setMaxResults(1);
        return query.getSingleResult();

    }

    public List<String> delete() {
        List<String> list = manager.createNativeQuery("select distinct topic_id from topic_indicator").getResultList();
        manager.createQuery("delete from Association").executeUpdate();
        manager.createNativeQuery("delete from topic_name_varients").executeUpdate();
        manager.createQuery("delete from Topic").executeUpdate();
        manager.createQuery("delete from CategoryAssociation").executeUpdate();
        manager.createQuery("delete from TopicMap").executeUpdate();
        return list;

    }

    public List<Topic> findAllFromMap(Long id) {
        TypedQuery<Topic> query = manager.createQuery("select tt from TopicMap t join t.topics tt where t.id =:id", Topic.class);
        query.setParameter("id", id);
        return query.getResultList();
    }

    public void save(CategoryAssociation assoc) {
        manager.persist(assoc);

    }

    public void deleteCategoryAssociations() {
        manager.createQuery("delete from CategoryAssociation").executeUpdate();
    }

    public List<CategoryAssociation> findAllCategoryAssociations() {
        return manager.createQuery("from CategoryAssociation", CategoryAssociation.class).getResultList();
    }

    public Collection<? extends String> findRelatedTopics(String tid) {
        Set<String> assoc = new HashSet<>();
        TopicMap map = manager.createQuery("from TopicMap", TopicMap.class).getSingleResult();

        map.getAssociations().forEach(asc -> {
            if (StringUtils.equals(asc.getFrom().getIdentifier(), tid)) {
                assoc.add(asc.getTo().getIdentifier());
                assoc.add(asc.getIdentifier());
            }
            if (StringUtils.equals(asc.getTo().getIdentifier(), tid)) {
                assoc.add(asc.getFrom().getIdentifier());
                assoc.add(asc.getIdentifier());
            }
            if (StringUtils.equals(asc.getIdentifier(), tid)) {
                assoc.add(asc.getFrom().getIdentifier());
                assoc.add(asc.getTo().getIdentifier());
            }
        });
        return assoc;
    }

}
