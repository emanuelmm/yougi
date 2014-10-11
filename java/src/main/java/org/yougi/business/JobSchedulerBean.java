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
package org.yougi.business;

import org.yougi.entity.*;
import org.yougi.reference.JobFrequencyType;
import org.yougi.reference.JobStatus;
import org.yougi.util.PackageResourceHelper;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class JobSchedulerBean extends AbstractBean<JobScheduler> {

    private static final Logger LOGGER = Logger.getLogger(JobSchedulerBean.class.getSimpleName());

    @PersistenceContext
    private EntityManager em;

    @EJB
    private JobExecutionBean jobExecutionBean;

    public JobSchedulerBean() {
        super(JobScheduler.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
	}

    public List<JobScheduler> findAllScheduled() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        return em.createQuery("select js from JobScheduler js where js.endDate is null or js.endDate >= :today order by js.startDate, js.startTime asc", JobScheduler.class)
                 .setParameter("today", today.getTime())
                 .getResultList();
    }

    public List<JobScheduler> findAllExpired() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.MINUTE, 59);
        today.set(Calendar.SECOND, 59);

        return em.createQuery("select js from JobScheduler js where js.endDate is not null and js.endDate < :today order by js.startDate, js.startTime desc", JobScheduler.class)
                .setParameter("today", today.getTime())
                .getResultList();
    }

    /**
     * This method is necessary because the JSR 352 specification doesn't offer a method to retrieve the list of
     * available jobs. We need this list to enable the scheduler to start jobs without changing the code for every new
     * job definition.
     * @return a list of job xml file names that are located in the /META-INF/batch-job directory.
     * */
    public List<String> getJobXmlNames() {
        List<String> names = new ArrayList<>();
        List<File> files = PackageResourceHelper.getFilesFolder("/META-INF/batch-jobs");
        for(File file: files) {
            if (file.getName().endsWith(".xml")) {
                names.add(file.getName().substring(0, file.getName().length() - 4));
            }
        }
        return names;
    }

    public JobScheduler getDefaultInstance() {
        return getInstance(JobFrequencyType.DAILY);
    }

    public JobScheduler getInstance(JobFrequencyType jobFrequencyType, JobScheduler toMerge) {
        JobScheduler jobScheduler = getInstance(jobFrequencyType);
        jobScheduler = merge(toMerge, jobScheduler);
        return jobScheduler;
    }

    public <T extends JobScheduler> T getInstance(JobFrequencyType jobFrequencyType, Class<T> jobSchedulerClass, JobScheduler toMerge) {
        JobScheduler jobScheduler = getInstance(jobFrequencyType);
        jobScheduler = merge(toMerge, jobScheduler);
        return jobSchedulerClass.cast(jobScheduler);
    }

    private JobScheduler merge(JobScheduler origin, JobScheduler destination) {
        destination.setId(origin.getId());
        destination.setName(origin.getName());
        if(destination.getFrequencyType() != JobFrequencyType.INSTANT) {
            destination.setStartDate(origin.getStartDate());
        }
        destination.setEndDate(origin.getEndDate());
        destination.setStartTime(origin.getStartTime());
        destination.setDescription(origin.getDescription());
        destination.setDefaultOwner(origin.getDefaultOwner());
        destination.setFrequency(origin.getFrequency());
        destination.setActive(origin.getActive());
        return destination;
    }

    public JobScheduler getInstance(JobFrequencyType jobFrequencyType) {
        JobScheduler jobScheduler;
        switch (jobFrequencyType) {
            case INSTANT:
                jobScheduler = new JobSchedulerInstant();
                break;
            case ONCE:
                jobScheduler = new JobSchedulerOnce();
                break;
            case HOURLY:
                jobScheduler = new JobSchedulerHourly();
                jobScheduler.setFrequency(1);
                break;
            case DAILY:
                jobScheduler = new JobSchedulerDaily();
                jobScheduler.setFrequency(1);
                break;
            case WEEKLY:
                jobScheduler = new JobSchedulerWeekly();
                jobScheduler.setFrequency(1);
                break;
            case MONTHLY:
                jobScheduler = new JobSchedulerMonthly();
                jobScheduler.setFrequency(1);
                break;
            case YEARLY:
                jobScheduler = new JobSchedulerYearly();
                jobScheduler.setFrequency(1);
                break;
            default: return null;
        }
        jobScheduler.setStartDate(Calendar.getInstance().getTime());
        jobScheduler.setActive(true);
        return jobScheduler;
    }

    /**
     * Persist a new job scheduler and immediately schedules its first execution. Do not use it to update an existing
     * job scheduler. Use the method update() instead.
     * */
    @Override
    public JobScheduler save(JobScheduler jobScheduler) {
        JobScheduler persistentJobScheduler = super.save(jobScheduler);

        jobExecutionBean.schedule(persistentJobScheduler);

        return persistentJobScheduler;
    }

    public void activate(JobScheduler jobScheduler) {
        if(!jobScheduler.getActive()) {
            jobScheduler.setActive(Boolean.TRUE);
            super.save(jobScheduler);
            this.jobExecutionBean.schedule(jobScheduler);
        }
    }

    public void deactivate(JobScheduler jobScheduler) {
        if(jobScheduler.getActive()) {
            jobScheduler.setActive(Boolean.FALSE);
            super.save(jobScheduler);

            List<JobExecution> scheduledExecutions = jobExecutionBean.findJobExecutions(jobScheduler, JobStatus.SCHEDULED);
            for(JobExecution jobExecution: scheduledExecutions) {
                Timer timer = jobExecutionBean.findTimer(jobExecution);
                timer.cancel();
            }
        }
    }
}