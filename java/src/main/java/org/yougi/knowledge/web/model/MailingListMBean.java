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
package org.yougi.knowledge.web.model;

import org.yougi.knowledge.entity.MailingList;
import org.yougi.annotation.ManagedProperty;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
public class MailingListMBean {

    @EJB
    private org.yougi.knowledge.business.MailingListBean mailingListBean;

    @Inject
    @ManagedProperty("#{param.id}")
    private String id;

    private MailingList mailingList;

    private List<MailingList> mailingLists;

    public MailingListMBean() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MailingList getMailingList() {
        return mailingList;
    }

    public void setMailingList(MailingList mailingList) {
        this.mailingList = mailingList;
    }

    public List<MailingList> getMailingLists() {
        if(this.mailingLists == null) {
            this.mailingLists = mailingListBean.findMailingLists();
        }
        return this.mailingLists;
    }

    @PostConstruct
    public void load() {
        if(id != null && !id.isEmpty()) {
            this.mailingList = mailingListBean.find(id);
        } else {
            this.mailingList = new MailingList();
        }
    }

    public String save() {
        mailingListBean.save(this.mailingList);
        return "mailing_lists?faces-redirect=true";
    }

    public String remove() {
        mailingListBean.remove(this.mailingList.getId());
        return "mailing_lists?faces-redirect=true";
    }
}