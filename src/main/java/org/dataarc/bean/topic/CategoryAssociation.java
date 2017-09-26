package org.dataarc.bean.topic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.dataarc.bean.AbstractPersistable;

@Entity
@Table(name="topic_category_association")
public class CategoryAssociation extends AbstractPersistable {

    private static final long serialVersionUID = -780915672049150822L;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Enumerated(EnumType.STRING)
    @Column(length = 255)
    private TopicCategory category;

    public CategoryAssociation() {}
    
    public CategoryAssociation(Topic findById, TopicCategory topicCategory) {
        this.topic = findById;
        this.category = topicCategory;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public TopicCategory getCategory() {
        return category;
    }

    public void setCategory(TopicCategory category) {
        this.category = category;
    }

}
