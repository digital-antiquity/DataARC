package org.dataarc.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dataarc.bean.topic.Topic;

/**
 * Wrapper for the topic map for serialization (only serialize what we need)
 * 
 * @author abrin
 *
 */
public class TopicMapWrapper {

    private Topic root;
    private Collection<Topic> topicCategories;

    public TopicMapWrapper(Collection<Topic> roots, Topic root) {
        this.root = root;
        this.topicCategories = roots;
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        for (Topic child : topicCategories) {
            categories.add(child.getName());
        }
        return categories;
    }

    public List<TopicWrapper> getTopics() {
        List<TopicWrapper> topicWrappers = new ArrayList<>();
        for (Topic cat : topicCategories) {
            for (Topic child : cat.getChildren()) {
                topicWrappers.add(new TopicWrapper(child, cat));
            }
        }
        return topicWrappers;
    }
}
