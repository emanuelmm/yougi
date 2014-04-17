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
package org.cejug.yougi.business;

import org.cejug.yougi.entity.*;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class JobSchedulerBean extends AbstractBean<JobScheduler> {

    private static final Logger LOGGER = Logger.getLogger(JobSchedulerBean.class.getSimpleName());

	@PersistenceContext
    private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public JobSchedulerBean() {
		super(JobScheduler.class);
	}
	
	public List<JobScheduler> findAll() {
		return em.createQuery("select js from JobScheduler js order by js.name asc", JobScheduler.class).getResultList();
	}

    public List<String> findUnscheduledJobNames() {
        List<JobScheduler> schedulers = findAll();
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Set<String> jobNames = jobOperator.getJobNames();

        if (jobNames.isEmpty()) {
            Job[] jobs = Job.values();
            for(Job job: jobs) {
                jobNames.add(job.toString());
            }
        }

        for(String jobName: jobNames) {
            for(JobScheduler jobScheduler: schedulers) {
                if(jobScheduler.getName().equals(jobName)) {
                    jobNames.remove(jobName);
                    break;
                }
            }
        }

        return new ArrayList<>(jobNames);
    }

    public JobScheduler getDefaultInstance() {
        return getInstance(JobFrequencyType.INSTANT);
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

    private JobScheduler merge(JobScheduler origin, JobScheduler destine) {
        destine.setId(origin.getId());
        destine.setName(origin.getName());
        destine.setStartDate(origin.getStartDate());
        destine.setEndDate(origin.getEndDate());
        destine.setStartTime(origin.getStartTime());
        destine.setDescription(origin.getDescription());
        destine.setDefaultOwner(origin.getDefaultOwner());
        destine.setFrequency(origin.getFrequency());
        destine.setActive(origin.getActive());
        return destine;
    }

    public JobScheduler getInstance(JobFrequencyType jobFrequencyType) {
        JobScheduler jobScheduler;
        switch (jobFrequencyType) {
            case INSTANT:
                jobScheduler = new JobInstantScheduler();
                break;
            case ONCE:
                jobScheduler = new JobOnceScheduler();
                break;
            case DAILY:
                jobScheduler = new JobDailyScheduler();
                break;
            case WEEKLY:
                jobScheduler = new JobWeeklyScheduler();
                break;
            case MONTHLY:
                jobScheduler = new JobMonthlyScheduler();
                break;
            case YEARLY:
                jobScheduler = new JobYearlyScheduler();
                break;
            default: return null;
        }
        return jobScheduler;
    }
}