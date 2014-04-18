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

import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.*;
import org.cejug.yougi.business.JobSchedulerBean;
import org.cejug.yougi.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class JobSchedulerMBean {

    private static final Logger LOGGER = Logger.getLogger(JobSchedulerMBean.class.getSimpleName());

    @EJB
    private JobSchedulerBean jobSchedulerBean;

    @EJB
    private UserAccountBean userAccountBean;

    private JobScheduler jobScheduler;

    private List<JobScheduler> jobSchedulers;
    private List<String> jobNames;
    private List<UserAccount> userAccounts;

    private String selectedOwner;
    private JobFrequencyType frequencyType;
    private Boolean workingDaysOnly;
    private Date startDate;
    private Date startTime;
    private Date endDate;
    private Integer frequency;

    @ManagedProperty(value="#{param.id}")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String userId) {
        this.id = userId;
    }

    public JobScheduler getJobScheduler() {
        return jobScheduler;
    }

    public String getSelectedOwner() {
        return selectedOwner;
    }

    public void setSelectedOwner(String selectedOwner) {
        this.selectedOwner = selectedOwner;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public JobFrequencyType getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(JobFrequencyType frequencyType) {
        LOGGER.log(Level.INFO, "Frequency type: {0}", frequencyType);
        this.frequencyType = frequencyType;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        LOGGER.log(Level.INFO, "Frequency0: {0}", frequency);
        this.frequency = frequency;
    }

    public Boolean getWorkingDaysOnly() {
        return workingDaysOnly;
    }

    public void setWorkingDaysOnly(Boolean workingDaysOnly) {
        this.workingDaysOnly = workingDaysOnly;
    }

    public List<JobScheduler> getJobSchedulers() {
        if(this.jobSchedulers == null) {
            this.jobSchedulers = jobSchedulerBean.findAll();
        }
        return this.jobSchedulers;
    }

    public List<String> getJobNames() {
        if(this.jobNames == null) {
            this.jobNames = jobSchedulerBean.findUnscheduledJobNames();
        }
        return this.jobNames;
    }

    public List<UserAccount> getUserAccounts() {
        if(this.userAccounts == null) {
            this.userAccounts = userAccountBean.findAllActiveAccounts();
        }
        return this.userAccounts;
    }

    @PostConstruct
    public void load() {
        if(EntitySupport.INSTANCE.isIdValid(this.id)) {
            this.jobScheduler = jobSchedulerBean.find(this.id);
        }
        else {
            this.jobScheduler = jobSchedulerBean.getDefaultInstance();
            jobScheduler.setActive(true);
        }
    }

    public String save() {
        if(!StringUtils.INSTANCE.isNullOrBlank(this.selectedOwner)) {
            UserAccount owner = userAccountBean.find(this.selectedOwner);
            this.jobScheduler.setDefaultOwner(owner);
        }

        if(this.frequencyType == JobFrequencyType.INSTANT) {
            jobScheduler = jobSchedulerBean.getInstance(this.frequencyType, JobInstantScheduler.class, this.jobScheduler);
            jobScheduler.setStartDate(Calendar.getInstance().getTime());
        }
        else if(this.frequencyType == JobFrequencyType.DAILY) {
            JobDailyScheduler jobDailyScheduler = jobSchedulerBean.getInstance(this.frequencyType, JobDailyScheduler.class, this.jobScheduler);
            jobDailyScheduler.setWorkingDay(this.getWorkingDaysOnly());
            jobDailyScheduler.setStartDate(startDate);
            jobScheduler = jobDailyScheduler;
        }
        else {
            jobScheduler = jobSchedulerBean.getInstance(this.frequencyType, this.jobScheduler);
            jobScheduler.setStartDate(startDate);
        }

        if(startTime != null) {
            jobScheduler.setStartTime(startTime);
        }

        if(endDate != null) {
            jobScheduler.setEndDate(endDate);
        }
        LOGGER.log(Level.INFO, "Frequency1: {0}", frequency);
        jobScheduler.setFrequency(frequency);
        LOGGER.log(Level.INFO, "Frequency2: {0}", jobScheduler.getFrequency());
        jobSchedulerBean.save(jobScheduler);

        return "job_schedulers";
    }

    public String remove() {
        jobSchedulerBean.remove(this.jobScheduler.getId());
        return "job_schedulers";
    }
}