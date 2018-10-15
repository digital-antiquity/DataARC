package org.dataarc.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import org.dataarc.bean.topic.Topic;

/**
 * Create a relationship between topics and indicators and allows us to model confidence if we need it. Could be replaced with a @ManyToMany if we don't use the
 * confidence metric
 * 
 * @author abrin
 *
 */
@Entity(name = "topic_indicator")
public class TopicIndicatorAssociation extends AbstractPersistable {

    private static final long serialVersionUID = 4025500335159592842L;

    @ManyToOne
    private Topic topic;

    @ManyToOne
    private Combinator indicator;

    @Enumerated(EnumType.STRING)
    @Column(length = 255)
    private Confidence confidence;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Combinator getIndicator() {
        return indicator;
    }

    public void setIndicator(Combinator indicator) {
        this.indicator = indicator;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public void setConfidence(Confidence confidence) {
        this.confidence = confidence;
    }

}
