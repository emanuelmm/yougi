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
package org.cejug.yougi.web.controller;

import org.cejug.yougi.business.CommunityBean;
import org.cejug.yougi.entity.Community;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class CommunityMBean {

    @EJB
    private CommunityBean communityBean;

    @ManagedProperty(value = "#{param.id}")
    private String id;

    private Community community;

    private List<Community> communities;

    public CommunityMBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Community getCommunity() {
        return community;
    }

    public List<Community> getCommunities() {
        if(this.communities == null) {
            this.communities = communityBean.findAll();
        }
        return this.communities;
    }

    @PostConstruct
    public void load() {
        if (id != null && !id.isEmpty()) {
            this.community = communityBean.find(id);
        } else {
            this.community = new Community();
        }
    }

    public String save() {
        communityBean.save(this.community);
        return "communities?faces-redirect=true";
    }

    public String remove() {
        communityBean.remove(this.community.getId());
        return "communities?faces-redirect=true";
    }
}