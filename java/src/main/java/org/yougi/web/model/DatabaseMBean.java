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
package org.yougi.web.model;

import org.yougi.business.DatabaseChangeLogBean;
import org.yougi.entity.DatabaseChangeLog;
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
public class DatabaseMBean {

    @EJB
    private DatabaseChangeLogBean databaseChangeLogBean;

    private List<DatabaseChangeLog> databaseChangeLogs;

    private DatabaseChangeLog databaseChangeLog;

    @Inject
    @ManagedProperty("#{param.id}")
    private String id;

    public DatabaseMBean() {
    }

    @PostConstruct
    public void load() {
        if (id != null && !id.isEmpty()) {
            this.databaseChangeLog = databaseChangeLogBean.find(id);
        } else {
            this.databaseChangeLog = new DatabaseChangeLog();
        }
    }

    public DatabaseChangeLog getDatabaseChangeLog() {
        return this.databaseChangeLog;
    }

    public List<DatabaseChangeLog> getDatabaseChangeLogs() {
        if(this.databaseChangeLogs == null) {
            this.databaseChangeLogs = databaseChangeLogBean.findAll();
        }
        return this.databaseChangeLogs;
    }
}