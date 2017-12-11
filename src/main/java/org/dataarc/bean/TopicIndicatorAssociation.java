package org.dataarc.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import org.dataarc.bean.topic.Topic;

@Entity(name = "topic_indicator")
public class TopicIndicatorAssociation extends AbstractPersistable {

    private static final long serialVersionUID = 4025500335159592842L;

    @ManyToOne
    private Topic topic;

    @ManyToOne
    private Indicator indicator;

    @Enumerated(EnumType.STRING)
    @Column(length = 255)
    private Confidence confidence;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public void setConfidence(Confidence confidence) {
        this.confidence = confidence;
    }

}
