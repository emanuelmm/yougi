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
import org.cejug.yougi.exception.BusinessLogicException;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
        JobExecution persistentJobExecution = super.save(jobExecution);

        timerService.createTimer(persistentJobExecution.getStartTime(), persistentJobExecution.getId());

        return persistentJobExecution;
    }

    @Timeout
    public void startJob(Timer timer) {
        // Retrieves the job execution from the database.
        String jobExecutionId = (String) timer.getInfo();
        JobExecution currentJobExecution = find(jobExecutionId);
        JobScheduler jobScheduler = currentJobExecution.getJobScheduler();

        // Starts the job execution.
        currentJobExecution.startJob();

        // Schedules the next job execution.
        try {
            if(jobScheduler.getActive()) {
                JobExecution nextJobExecution = jobScheduler.getNextJobExecution();
                this.save(nextJobExecution);
            }
        } catch (BusinessLogicException e) {
            LOGGER.log(Level.WARNING, "Not possible to create the next job execution.");
        }
    }
}