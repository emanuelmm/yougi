/* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * There is a full copy of the GNU Lesser General Public License along with
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.cejug.yougi.knowledge.web.controller;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.knowledge.business.ArticleBean;
import org.cejug.yougi.knowledge.business.WebSourceBean;
import org.cejug.yougi.knowledge.entity.Article;
import org.cejug.yougi.knowledge.entity.WebSource;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class WebSourceMBean {

    @EJB
    private UserAccountBean userAccountBean;

    @EJB
    private WebSourceBean webSourceBean;

    @EJB
    private ArticleBean articleBean;

    private WebSource webSource;

    private List<WebSource> webSources;
    private List<Article> publishedArticles;

    private List<UserAccount> membersWithWebsite;
    private String selectedMember;
    private String website;

    @ManagedProperty(value="#{param.id}")
    private String id;

    public WebSource getWebSource() {
        return this.webSource;
    }

    public String getId() {
        return id;
    }

    public void setId(String userId) {
        this.id = userId;
    }

    public List<WebSource> getWebSources() {
        if(this.webSources == null) {
            this.webSources = webSourceBean.findWebResources();
        }
        return this.webSources;
    }

    public List<UserAccount> getMembersWithWebsite() {
        if(this.membersWithWebsite == null) {
            this.membersWithWebsite = webSourceBean.findNonReferencedProviders();
        }
        return this.membersWithWebsite;
    }

    public String getSelectedMember() {
        return this.selectedMember;
    }

    public void setSelectedMember(String selectedMember) {
        this.selectedMember = selectedMember;
    }

    public String getWebsite() {
        if(this.selectedMember != null && !this.selectedMember.isEmpty() && this.website == null) {
            this.webSource.setProvider(userAccountBean.find(this.selectedMember));
            this.website = this.webSource.getProvider().getWebsite();
            this.website = webSourceBean.setProtocol(this.website);
        }
        return this.website;
    }

    public String getTitle() {
        if(this.selectedMember != null && !this.selectedMember.isEmpty() && this.webSource.getTitle() == null) {
            this.webSource = webSourceBean.loadWebSource(this.webSource);
        }
        return this.webSource.getTitle();
    }

    public void setTitle(String title) {
        this.webSource.setTitle(title);
    }

    public String getFeed() {
        if(this.selectedMember != null && !this.selectedMember.isEmpty() && this.webSource.getFeed() == null) {
            this.webSource = webSourceBean.loadWebSource(this.webSource);
        }
        return this.webSource.getFeed();
    }

    public void setFeed(String feed) {
        this.webSource.setFeed(feed);
    }

    public List<Article> getPublishedArticles() {
        if(publishedArticles == null) {
            this.publishedArticles = articleBean.findPublishedArticles(this.webSource);
        }
        return this.publishedArticles;
    }

    @PostConstruct
    public void load() {
        if(this.id != null && !this.id.isEmpty()) {
            this.webSource = webSourceBean.find(this.id);
            this.selectedMember = this.webSource.getProvider().getId();
        }
        else {
            this.webSource = new WebSource();
        }
    }

    public String save() {
        if(this.selectedMember != null && !this.selectedMember.isEmpty()) {
            this.webSource.setProvider(userAccountBean.find(this.selectedMember));
        }
        webSourceBean.save(this.webSource);
        return "web_sources";
    }

    public String undoReference() {
        webSourceBean.remove(this.webSource.getId());
        this.webSource.setId(null);
        return "website";
    }

    public String refreshUnpublishedContent() {
        return "website";
    }
}