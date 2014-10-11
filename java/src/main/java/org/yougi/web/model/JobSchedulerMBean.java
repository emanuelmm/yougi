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

import org.yougi.business.JobExecutionBean;
import org.yougi.business.JobSchedulerBean;
import org.yougi.business.UserAccountBean;
import org.yougi.entity.*;
import org.yougi.reference.JobFrequencyType;
import org.yougi.reference.JobStatus;
import org.yougi.util.DateTimeUtils;
import org.yougi.util.EntitySupport;
import org.yougi.util.ResourceBundleHelper;
import org.yougi.annotation.ManagedProperty;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
public class JobSchedulerMBean {

    private static final Logger LOGGER = Logger.getLogger(JobSchedulerMBean.class.getSimpleName());

    @EJB
    private JobSchedulerBean jobSchedulerBean;

    @EJB
    private JobExecutionBean jobExecutionBean;

    @EJB
    private UserAccountBean userAccountBean;

    private List<JobScheduler> jobsScheduled;
    private List<JobScheduler> jobsExpired;
    private List<JobExecution> jobExecutions;
    private List<String> jobNames;
    private List<UserAccount> userAccounts;

    private Boolean workingHoursOnly;

    @Inject
    @ManagedProperty("#{param.id}")
    private String id;

    @Inject
    private JobScheduleMBean jobScheduleMBean;

    private Date startDate;
    private Date startTime;
    private Date endDate;
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

    public JobSchedulerHourly getJobSchedulerHourly() {
        JobScheduler jobScheduler = this.getJobScheduler();
        if(jobScheduler instanceof JobSchedulerHourly) {
            return (JobSchedulerHourly) jobScheduler;
        }
        return null;
    }

    public JobSchedulerDaily getJobSchedulerDaily() {
        JobScheduler jobScheduler = this.getJobScheduler();
        if(jobScheduler instanceof JobSchedulerDaily) {
            return (JobSchedulerDaily) jobScheduler;
        }
        return null;
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

    public Boolean getWorkingHoursOnly() {
        return workingHoursOnly;
    }

    public void setWorkingHoursOnly(Boolean workingHoursOnly) {
        this.workingHoursOnly = workingHoursOnly;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<JobScheduler> getJobsScheduled() {
        if(this.jobsScheduled == null) {
            this.jobsScheduled = jobSchedulerBean.findAllScheduled();
        }
        return this.jobsScheduled;
    }

    public List<JobScheduler> getJobsExpired() {
        if(this.jobsExpired == null) {
            this.jobsExpired = jobSchedulerBean.findAllExpired();
        }
        return this.jobsExpired;
    }

    public List<JobExecution> getJobExecutions() {
        if(this.jobExecutions == null) {
            this.jobExecutions = jobExecutionBean.findJobExecutions(this.jobScheduleMBean.getJobScheduler());
            for(JobExecution jobExecution : this.jobExecutions) {
                if(jobExecution.getStatus().equals(JobStatus.SCHEDULED)) {
                    jobExecution.setTimeout(jobExecutionBean.findTimeout(jobExecution));
                }
            }
        }

        return jobExecutions;
    }

    public List<String> getJobNames() {
        if(this.jobNames == null) {
            this.jobNames = jobSchedulerBean.getJobXmlNames();

            // If the job is already scheduled it is removed from the list.
            List<JobScheduler> scheduled = jobSchedulerBean.findAllScheduled();
            for(JobScheduler jobScheduler : scheduled) {
                for(String jobName: this.jobNames) {
                    if(jobName.equals(jobScheduler.getName())) {
                        jobNames.remove(jobName);
                        break;
                    }
                }
            }
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
        this.endDate = (Date)value;
        LOGGER.log(Level.INFO, "endDate {0}", endDate);
    }

    public void validateEndTime(FacesContext context, UIComponent component, Object value) {
        this.endTime = (Date) value;
        LOGGER.log(Level.INFO, "endTime {0}", this.endTime);

        Date startDateAndTime = DateTimeUtils.mergeDateAndTime(this.startDate, this.startTime);
        LOGGER.log(Level.INFO, "startDateAndTime {0}", startDateAndTime);

        Date endDateAndTime = DateTimeUtils.mergeDateAndTime(this.endDate, this.endTime);
        LOGGER.log(Level.INFO, "endDateAndTime {0}", endDateAndTime);

        if (startDateAndTime.compareTo(endDateAndTime) > 0) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleHelper.getMessage("errorCode0015"), null));
        }
    }

    @PostConstruct
    public void load() {
        if(EntitySupport.isIdValid(this.id)) {
            this.jobScheduleMBean.loadJobScheduler(this.id);

            if(this.jobScheduleMBean.getJobScheduler() instanceof JobSchedulerHourly) {
                JobSchedulerHourly jobSchedulerHourly = (JobSchedulerHourly) this.jobScheduleMBean.getJobScheduler();
                this.workingHoursOnly = jobSchedulerHourly.getWorkingHoursOnly();
                this.endTime = jobSchedulerHourly.getEndTime();
            }
            else if(this.jobScheduleMBean.getJobScheduler() instanceof JobSchedulerDaily) {
                JobSchedulerDaily jobSchedulerDaily = (JobSchedulerDaily) this.jobScheduleMBean.getJobScheduler();
                this.workingHoursOnly = jobSchedulerDaily.getWorkingDaysOnly();
            }
        }
    }

    public String save() {
        if(this.jobScheduleMBean.getJobScheduler().getFrequencyType() == JobFrequencyType.HOURLY) {
            JobSchedulerHourly jobSchedulerHourly = (JobSchedulerHourly) jobScheduleMBean.getJobScheduler();
            jobSchedulerHourly.setWorkingHoursOnly(this.workingHoursOnly);
            jobSchedulerHourly.setEndTime(this.endTime);
        }
        else if(this.jobScheduleMBean.getJobScheduler().getFrequencyType() == JobFrequencyType.DAILY) {
            JobSchedulerDaily jobSchedulerDaily = (JobSchedulerDaily) jobScheduleMBean.getJobScheduler();
            jobSchedulerDaily.setWorkingDaysOnly(this.workingHoursOnly);
        }

        if(EntitySupport.isIdNotValid(jobScheduleMBean.getJobScheduler())) {
            jobSchedulerBean.save(jobScheduleMBean.getJobScheduler());
        } else {
            jobSchedulerBean.update(jobScheduleMBean.getJobScheduler());
        }

        return "job_schedulers";
    }

    public String activate() {
        String id = this.jobScheduleMBean.getJobScheduler().getId();
        JobScheduler jobScheduler = jobSchedulerBean.find(id);
        jobSchedulerBean.activate(jobScheduler);
        return "job_scheduler?faces-redirect=true&id="+ id;
    }

    public String deactivate() {
        String id = this.jobScheduleMBean.getJobScheduler().getId();
        JobScheduler jobScheduler = jobSchedulerBean.find(id);
        jobSchedulerBean.deactivate(jobScheduler);
        return "job_scheduler?faces-redirect=true&id="+ id;
    }

    public String schedule() {
        String id = this.jobScheduleMBean.getJobScheduler().getId();
        JobScheduler jobScheduler = jobSchedulerBean.find(id);
        jobExecutionBean.schedule(jobScheduler);
        return "job_scheduler?faces-redirect=true&id="+ id;
    }

    public String remove() {
        jobSchedulerBean.remove(this.jobScheduleMBean.getJobScheduler().getId());
        return "job_schedulers";
    }
}