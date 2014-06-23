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

import org.cejug.yougi.business.JobExecutionBean;
import org.cejug.yougi.business.JobSchedulerBean;
import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.*;
import org.cejug.yougi.util.DateTimeUtils;
import org.cejug.yougi.util.ResourceBundleHelper;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
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
    private JobExecutionBean jobExecutionBean;

    @EJB
    private UserAccountBean userAccountBean;

    private List<JobScheduler> jobSchedulers;
    private List<JobExecution> jobExecutions;
    private List<String> jobNames;
    private List<UserAccount> userAccounts;

    private Boolean workingDaysOnly;

    @ManagedProperty(value="#{param.id}")
    private String id;

    @ManagedProperty(value = "#{jobScheduleMBean}")
    private JobScheduleMBean jobScheduleMBean;

    private Date startDate;
    private Date startTime;
    private Date endTime;

    public void setId(String userId) {
        this.id = userId;
    }

    public void setJobScheduleMBean(JobScheduleMBean jobScheduleMBean) {
        this.jobScheduleMBean = jobScheduleMBean;
    }

    public JobScheduler getJobScheduler() {
        return this.jobScheduleMBean.getJobScheduler();
    }

    public String getSelectedOwner() {
        UserAccount userAccount = jobScheduleMBean.getDefaultOwner();
        if(userAccount != null) {
            return userAccount.getId();
        } else {
            return null;
        }
    }

    public void setSelectedOwner(String selectedOwner) {
        jobScheduleMBean.setDefaultOwner(selectedOwner);
    }

    public JobFrequencyType getFrequencyType() {
        return jobScheduleMBean.getJobScheduler().getFrequencyType();
    }

    public void setFrequencyType(JobFrequencyType frequencyType) {
        this.jobScheduleMBean.changeJobFrequencyType(frequencyType);
    }

    public Boolean getWorkingDaysOnly() {
        return workingDaysOnly;
    }

    public void setWorkingDaysOnly(Boolean workingDaysOnly) {
        this.workingDaysOnly = workingDaysOnly;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<JobScheduler> getJobSchedulers() {
        if(this.jobSchedulers == null) {
            this.jobSchedulers = jobSchedulerBean.findAllActive();
        }
        return this.jobSchedulers;
    }

    public List<JobExecution> getJobExecutions() {
        if(this.jobExecutions == null) {
            this.jobExecutions = jobExecutionBean.findJobExecutions(this.jobScheduleMBean.getJobScheduler());
        }

        return jobExecutions;
    }

    public List<String> getJobNames() {
        if(this.jobNames == null) {
            this.jobNames = jobSchedulerBean.findJobNames();
        }
        return this.jobNames;
    }

    public List<UserAccount> getUserAccounts() {
        if(this.userAccounts == null) {
            this.userAccounts = userAccountBean.findAllActiveAccounts();
        }
        return this.userAccounts;
    }

    public void validateStartDate(FacesContext context, UIComponent component, Object value) {
        this.startDate = (Date)value;
        LOGGER.log(Level.INFO, "startDate {0}", this.startDate);
    }

    public void validateStartTime(FacesContext context, UIComponent component, Object value) {
        this.startTime = (Date)value;
        LOGGER.log(Level.INFO, "startTime {0}", this.startTime);
    }

    public void validateEndDate(FacesContext context, UIComponent component, Object value) {
        Date endDate = (Date)value;

        Date startDateAndTime = DateTimeUtils.INSTANCE.mergeDateAndTime(startDate, startTime);
        LOGGER.log(Level.INFO, "startDateAndTime {0}", startDateAndTime);
        LOGGER.log(Level.INFO, "endDate {0}", endDate);
        if (startDateAndTime.compareTo(endDate) > 0) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleHelper.INSTANCE.getMessage("errorCode0015"), null));
        }
    }

    @PostConstruct
    public void load() {
        if(EntitySupport.INSTANCE.isIdValid(this.id)) {
            this.jobScheduleMBean.loadJobScheduler(this.id);
        }
    }

    public String save() {
        if(this.jobScheduleMBean.getJobScheduler().getFrequencyType() == JobFrequencyType.DAILY) {
            JobSchedulerDaily jobSchedulerDaily = (JobSchedulerDaily) jobScheduleMBean.getJobScheduler();
            jobSchedulerDaily.setWorkingDaysOnly(this.workingDaysOnly);
        }

        if(EntitySupport.INSTANCE.isIdNotValid(jobScheduleMBean.getJobScheduler())) {
            jobSchedulerBean.save(jobScheduleMBean.getJobScheduler());
        } else {
            jobSchedulerBean.update(jobScheduleMBean.getJobScheduler());
        }

        return "job_schedulers";
    }

    public String remove() {
        jobSchedulerBean.remove(this.jobScheduleMBean.getJobScheduler().getId());
        return "job_schedulers";
    }
}