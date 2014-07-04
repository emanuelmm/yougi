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
import org.yougi.exception.EnvironmentResourceException;
import org.jboss.vfs.TempFileProvider;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
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

        return em.createQuery("select js from JobScheduler js where js.endDate is null or js.endDate >= :today order by js.startDate, js.startTime desc", JobScheduler.class)
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
     * @return list of job names that are not scheduled at the moment.
     * */
    public List<String> findJobNames() {
        Set<String> jobNames = getJobXmlNames();
        return new ArrayList<>(jobNames);
    }

    /**
     * This method is necessary because the JSR 352 specification doesn't offer a method to retrieve the list of
     * available jobs. We need this list to enable the scheduler to start jobs without changing the code for every new
     * job definition.
     * @return a set of job xml file names that are located in the /META-INF/batch-job directory.
     * */
    private Set<String> getJobXmlNames() {
        final ClassLoader loader = JobSchedulerBean.class.getClassLoader();
        URL url = loader.getResource("/META-INF/batch-jobs");

        if (url == null) {
            return Collections.emptySet();
        }

        VirtualFile virtualFile;
        Closeable handle = null;
        String protocol = url.getProtocol();
        Set<String> names = new HashSet<>();
        try {
            if ("vfs".equals(protocol)) {
                URLConnection conn = url.openConnection();
                virtualFile = (VirtualFile) conn.getContent();
            } else if ("file".equals(protocol)) {
                virtualFile = VFS.getChild(url.toURI());
                File archiveFile = virtualFile.getPhysicalFile();
                TempFileProvider provider = TempFileProvider.create("tmp", Executors.newScheduledThreadPool(2));
                handle = VFS.mountZip(archiveFile, virtualFile, provider);
            } else {
                throw new UnsupportedOperationException("Protocol " + protocol + " is not supported");
            }

            List<VirtualFile> files = virtualFile.getChildren();
            for(VirtualFile ccFile : files) {
                if (ccFile.getName().endsWith(".xml")) {
                    names.add(ccFile.getName().substring(0, ccFile.getName().length() - 4));
                }
            }

            if(handle != null) {
                handle.close();
            }
        } catch (IOException | URISyntaxException ioe) {
            throw new EnvironmentResourceException(ioe.getMessage(), ioe);
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

        JobExecution jobExecution = persistentJobScheduler.getJobExecution();
        LOGGER.log(Level.INFO, "Job Execution: {0}.", jobExecution.toString());
        jobExecutionBean.save(jobExecution);

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

            List<JobExecution> scheduledExecutions = jobExecutionBean.findExecutionJobs(JobStatus.SCHEDULED);
            for(JobExecution jobExecution: scheduledExecutions) {
                Timer timer = jobExecutionBean.findTimer(jobExecution);
                timer.cancel();
            }
        }
    }
}