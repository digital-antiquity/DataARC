package org.dataarc.bean;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.dataarc.core.search.query.SearchQueryObject;
import org.dataarc.util.hibernate.type.SearchJsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@Table(name = "state")
@Entity
@TypeDefs({ @TypeDef(name = "SearchQueryObject", typeClass = SearchJsonType.class) })
public class SavedSearch extends AbstractPersistable {

    private static final long serialVersionUID = -4107594961720051504L;

    @Column()
    @Type(type = "SearchQueryObject")
    private SearchQueryObject data;

    @Column(length = 255)
    private String uid = UUID.randomUUID().toString();

    @ManyToOne()
    @JoinColumn(name = "parent_id")
    private SavedSearch parent;

    @Column(length = 512)
    private String title;
    
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private DataArcUser user;

    @Column()
    private Long views;

    public SavedSearch() {}
    
    public SavedSearch(String title, SearchQueryObject query_, DataArcUser user) {
        this.setTitle(title);
        this.data = query_;
        this.setUser(user);
    }

    public SearchQueryObject getData() {
        return data;
    }

    public void setData(SearchQueryObject data) {
        this.data = data;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public SavedSearch getParent() {
        return parent;
    }

    public void setParent(SavedSearch parent) {
        this.parent = parent;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public DataArcUser getUser() {
        return user;
    }

    public void setUser(DataArcUser user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
