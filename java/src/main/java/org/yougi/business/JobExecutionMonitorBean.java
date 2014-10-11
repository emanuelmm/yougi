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
import org.yougi.reference.JobStatus;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.PersistenceException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Startup
@Singleton
@DependsOn("DatabaseMigrationBean")
public class JobExecutionMonitorBean {

    private static final Logger LOGGER = Logger.getLogger(JobExecutionMonitorBean.class.getSimpleName());

    @EJB
    private JobExecutionBean executionJobBean;

    @PostConstruct
    public void init() {
        try {
            List<JobExecution> scheduledJobExecutions = executionJobBean.findJobExecutions(JobStatus.SCHEDULED);
            Date timeout;
            for (JobExecution jobExecution : scheduledJobExecutions) {
                timeout = executionJobBean.findTimeout(jobExecution);
                if (timeout == null) {
                    timeout = executionJobBean.schedule(jobExecution);
                    jobExecution.setTimeout(timeout);
                }
            }
        } catch (PersistenceException e) {
            LOGGER.log(Level.WARNING, "Not possible to schedule active jobs during the application initialization: {0}.", e.getMessage());
        }
    }
}
