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

import org.yougi.business.JobSchedulerBean;
import org.yougi.business.UserAccountBean;
import org.yougi.reference.JobFrequencyType;
import org.yougi.entity.JobScheduler;
import org.yougi.entity.UserAccount;
import org.yougi.util.StringUtils;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@ViewScoped
public class JobScheduleMBean implements Serializable {

    @EJB
    private JobSchedulerBean jobSchedulerBean;

    @EJB
    private UserAccountBean userAccountBean;

    private JobScheduler jobScheduler;

    public JobScheduler getJobScheduler() {
        if(this.jobScheduler == null) {
            this.jobScheduler = jobSchedulerBean.getDefaultInstance();
        }
        return this.jobScheduler;
    }

    public void loadJobScheduler(String id) {
        this.jobScheduler = jobSchedulerBean.find(id);
    }

    public void changeJobFrequencyType(JobFrequencyType jobFrequencyType) {
        if(this.jobScheduler == null) {
            this.jobScheduler = jobSchedulerBean.getInstance(jobFrequencyType);
        } else if(!this.jobScheduler.getFrequencyType().equals(jobFrequencyType)) {
            this.jobScheduler = jobSchedulerBean.getInstance(jobFrequencyType, this.jobScheduler);
        }
    }

    public UserAccount getDefaultOwner() {
        if(this.jobScheduler != null && this.jobScheduler.getDefaultOwner() != null) {
            return this.jobScheduler.getDefaultOwner();
        } else {
            return null;
        }
    }

    public void setDefaultOwner(String selectedOwner) {
        if(!StringUtils.isNullOrBlank(selectedOwner)) {
            UserAccount owner = userAccountBean.find(selectedOwner);
            this.jobScheduler.setDefaultOwner(owner);
        }
    }
}