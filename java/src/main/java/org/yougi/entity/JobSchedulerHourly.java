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
package org.yougi.entity;

import org.yougi.reference.JobFrequencyType;
import org.yougi.util.DateTimeUtils;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Daily scheduled batch job.
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@DiscriminatorValue("HOURLY")
public class JobSchedulerHourly extends JobScheduler {

    private static final long serialVersionUID = 1L;

    @Column(name = "working_time")
    private Boolean workingHoursOnly;

    @Temporal(TemporalType.TIME)
    @Column(name = "end_time")
    private Date endTime;

    /**
     * If true, the job will run only during working hours, never during the night.
     * */
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

    @Override
    public JobExecution getNextJobExecution(UserAccount owner) {
        Calendar today = Calendar.getInstance();

        // Calculate original start time
        Calendar startTime = getJobExecutionStartTime();

        // If startTime is a date in the past then frequency is applied to it until it becomes bigger than today.
        while(today.compareTo(startTime) > 0) {
            startTime.add(Calendar.HOUR, this.getFrequency());
        }

        /* If the updated start time falls down out of working hours and the scheduler only considers working hours,
        * then the start time is incremented until it reaches the first working hour, which is 8h. The time is also
        * incremented until it reaches the first working day of the week, which is Monday. */
        if(workingHoursOnly) {
            while (startTime.get(Calendar.HOUR_OF_DAY) > 18 || startTime.get(Calendar.HOUR_OF_DAY) < 8) {
                startTime.add(Calendar.HOUR_OF_DAY, 1);
            }

            while (startTime.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                   startTime.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                startTime.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        Date endDateAndTime = DateTimeUtils.mergeDateAndTime(this.getEndDate(), this.getEndTime());

        if(this.getEndDate() != null && startTime.getTime().compareTo(endDateAndTime) > 0) {
            return null;
        }

        return new JobExecution(this, owner, startTime.getTime());
    }

    @Override
    public JobFrequencyType getFrequencyType() {
        return JobFrequencyType.HOURLY;
    }
}