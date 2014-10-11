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

import org.yougi.entity.JobExecution;
import org.yougi.reference.JobFrequencyType;
import org.yougi.entity.JobScheduler;
import org.yougi.reference.JobStatus;

import javax.annotation.Resource;
import javax.batch.operations.*;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class JobExecutionBean extends AbstractBean<JobExecution> {

    static final Logger LOGGER = Logger.getLogger(JobExecutionBean.class.getSimpleName());

    @PersistenceContext
    private EntityManager em;

    @Resource
    TimerService timerService;

    public JobExecutionBean() {
        super(JobExecution.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
	}

    public List<JobExecution> findJobExecutions(JobStatus jobStatus) {
        return em.createQuery("select je from JobExecution je where je.status = :status order by je.startTime asc", JobExecution.class)
                .setParameter("status", jobStatus)
                .getResultList();
    }

    public List<JobExecution> findJobExecutions(JobScheduler jobScheduler) {
        return em.createQuery("select je from JobExecution je where je.jobScheduler = :jobScheduler order by je.startTime desc", JobExecution.class)
                 .setParameter("jobScheduler", jobScheduler)
                 .getResultList();
    }

    public List<JobExecution> findJobExecutions(JobScheduler jobScheduler, JobStatus jobStatus) {
        return em.createQuery("select je from JobExecution je where je.jobScheduler = :jobScheduler and je.status = :status order by je.startTime asc", JobExecution.class)
                .setParameter("jobScheduler", jobScheduler)
                .setParameter("status", jobStatus)
                .getResultList();
    }

    public JobExecution findJobExecution(Long instanceId) {
        List<JobExecution> jobExecutions = em.createQuery("select je from JobExecution je where je.instanceId = :instanceId and je.status = :status order by je.startTime desc", JobExecution.class)
                                             .setParameter("instanceId", instanceId)
                                             .setParameter("status", JobStatus.STARTED)
                                             .getResultList();
        if(!jobExecutions.isEmpty()) {
            return jobExecutions.get(0);
        }
        return null;
    }

    public Date findTimeout(JobExecution jobExecution) {
        Timer timer = findTimer(jobExecution);
        return timer != null ? timer.getNextTimeout() : null;
    }

    public Timer findTimer(JobExecution jobExecution) {
        Collection<Timer> timers = timerService.getTimers();
        for(Timer timer : timers) {
            if(jobExecution.getId().compareTo((String) timer.getInfo()) == 0) {
                return timer;
            }
        }
        return null;
    }

    @Override
    public JobExecution save(JobExecution jobExecution) {
        JobExecution persistentJobExecution = null;
        if(jobExecution != null) {
            persistentJobExecution = super.save(jobExecution);
            schedule(persistentJobExecution);
        }
        return persistentJobExecution;
    }

    @Override
    public void remove(String id) {
        JobExecution jobExecution = find(id);
        Timer timer = findTimer(jobExecution);
        if(timer != null) {
            timer.cancel();
        }
        super.remove(id);
    }

    @Timeout
    public void startJob(Timer timer) {
        // Retrieves the job execution from the database.
        String jobExecutionId = (String) timer.getInfo();
        JobExecution currentJobExecution = find(jobExecutionId);
        if(currentJobExecution != null) {
            JobScheduler jobScheduler = currentJobExecution.getJobScheduler();

            // Starts the job execution.
            if (currentJobExecution.getStatus() == JobStatus.SCHEDULED) {
                currentJobExecution.setStartTime(Calendar.getInstance().getTime());
                startJob(currentJobExecution);
            }

            if(jobScheduler.getFrequencyType() != JobFrequencyType.ONCE && jobScheduler.getFrequencyType() != JobFrequencyType.INSTANT) {
                schedule(jobScheduler);
            } else {
                jobScheduler.setActive(Boolean.FALSE);
            }
        }
    }

    /**
     * Schedules the next job execution.
     */
    public void schedule(JobScheduler jobScheduler) {
        if(jobScheduler.getActive()) {
            List<JobExecution> scheduledExecutions = findJobExecutions(jobScheduler, JobStatus.SCHEDULED);
            if(scheduledExecutions == null || scheduledExecutions.isEmpty()) {
                JobExecution nextJobExecution = jobScheduler.getNextJobExecution();

                if(nextJobExecution == null) {
                    jobScheduler.setActive(Boolean.FALSE);
                    em.merge(jobScheduler);
                } else {
                    this.save(nextJobExecution);
                }
            } else {
                for(JobExecution jobExecution: scheduledExecutions) {
                    if(findTimeout(jobExecution) == null) {
                        Date timeout = schedule(jobExecution);
                        jobExecution.setTimeout(timeout);
                    }
                }
            }
        }
    }

    public Date schedule(JobExecution jobExecution) {
        Date startTime = jobExecution.getStartTime();
        Date now = Calendar.getInstance().getTime();

        if(startTime.compareTo(now) < 0) {
            startTime = now;
        }

        Timer timer = timerService.createTimer(startTime, jobExecution.getId());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        LOGGER.log(Level.INFO, "Job Scheduled to {0}", df.format(timer.getNextTimeout()));
        return timer.getNextTimeout();
    }

    public void startJob(JobExecution jobExecution) {
        try {
            JobOperator jo = BatchRuntime.getJobOperator();
            long instanceId = jo.start(jobExecution.getJobScheduler().getName(), new java.util.Properties());
            LOGGER.log(Level.INFO, "Started job: {0}", instanceId);
            jobExecution.setInstanceId(instanceId);
            jobExecution.setStatus(JobStatus.STARTED);
            em.merge(jobExecution);
        } catch (JobStartException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void stopJob(JobExecution jobExecution) {
        try {
            JobOperator jo = BatchRuntime.getJobOperator();
            jo.stop(jobExecution.getInstanceId());
            LOGGER.log(Level.INFO, "Stopped job: {0}", jobExecution.getInstanceId());
            jobExecution.setStatus(JobStatus.STOPPED);
            em.merge(jobExecution);
        } catch (JobExecutionNotRunningException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    public void restartJob(JobExecution jobExecution) {
        try {
            JobOperator jo = BatchRuntime.getJobOperator();
            jobExecution.setInstanceId(jo.restart(jobExecution.getInstanceId(), new java.util.Properties()));
            LOGGER.log(Level.INFO, "Restarted job: {0}", jobExecution.getInstanceId());
            jobExecution.setStatus(JobStatus.STARTED);
            em.merge(jobExecution);
        } catch (NoSuchJobExecutionException | JobRestartException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    public void abandonJob(JobExecution jobExecution) {
        JobOperator jo = BatchRuntime.getJobOperator();
        jo.abandon(jobExecution.getInstanceId());
        LOGGER.log(Level.INFO, "Abandoned job: {0}", jobExecution.getInstanceId());
        jobExecution.setStatus(JobStatus.ABANDONED);
        em.merge(jobExecution);
    }

    public void finalizeJob(long instanceId) {
        JobExecution jobExecution = findJobExecution(instanceId);
        jobExecution.setEndTime(Calendar.getInstance().getTime());
        jobExecution.setStatus(JobStatus.COMPLETED);
        save(jobExecution);
    }
}