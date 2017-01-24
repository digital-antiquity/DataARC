package org.dataarc.bean;

import org.dataarc.bean.topic.Topic;

public class TopicIndicatorAssociation extends AbstractPersistable {

    private Topic topic;

    private Indicator indicator;

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
