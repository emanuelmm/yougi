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

import org.cejug.yougi.entity.JobExecution;
import org.cejug.yougi.entity.JobScheduler;
import org.cejug.yougi.entity.JobStatus;
import org.cejug.yougi.exception.BusinessLogicException;

import javax.annotation.Resource;
import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.JobOperator;
import javax.batch.operations.JobRestartException;
import javax.batch.operations.JobStartException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    public List<JobExecution> findJobExecutions(JobScheduler jobScheduler) {
        return em.createQuery("select je from JobExecution je where je.jobScheduler = :jobScheduler order by je.startTime asc", JobExecution.class)
                 .setParameter("jobScheduler", jobScheduler)
                 .getResultList();
    }

    @Override
    public JobExecution save(JobExecution jobExecution) {
        JobExecution persistentJobExecution = null;
        if(jobExecution != null) {
            persistentJobExecution = super.save(jobExecution);
            Timer timer = timerService.createTimer(persistentJobExecution.getStartTime(), persistentJobExecution.getId());
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            LOGGER.log(Level.INFO, "Execution scheduled to {0}.", df.format(timer.getNextTimeout()));
        }
        return persistentJobExecution;
    }

    @Timeout
    public void startJob(Timer timer) {
        // Retrieves the job execution from the database.
        String jobExecutionId = (String) timer.getInfo();
        JobExecution currentJobExecution = find(jobExecutionId);
        JobScheduler jobScheduler = currentJobExecution.getJobScheduler();

        // Starts the job execution.
        startJob(currentJobExecution);

        // Schedules the next job execution.
        try {
            if(jobScheduler.getActive()) {
                JobExecution nextJobExecution = jobScheduler.getNextJobExecution();
                this.save(nextJobExecution);
            }
        } catch (BusinessLogicException e) {
            LOGGER.log(Level.WARNING, "Not possible to create the next job execution.", e);
        }
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
}