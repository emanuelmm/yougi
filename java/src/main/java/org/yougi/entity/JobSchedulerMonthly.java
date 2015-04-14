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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Calendar;

/**
 * Monthly scheduled batch job.
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@DiscriminatorValue("MONTHLY")
public class JobSchedulerMonthly extends JobScheduler {

	private static final long serialVersionUID = 1L;

    @Override
    public JobExecution getNextJobExecution(UserAccount owner) {
        Calendar today = Calendar.getInstance();

        // Calculate original start time
        Calendar startTime = getJobExecutionStartTime();

        // If startTime is a date in the past then frequency is applied to it until it becomes bigger than today.
        while(today.compareTo(startTime) > 0) {
            startTime.add(Calendar.MONTH, this.getFrequency());
        }

        if(this.getEndDate() != null) {
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(this.getEndDate());
            endDate.set(Calendar.HOUR_OF_DAY, 23);
            endDate.set(Calendar.MINUTE, 59);
            endDate.set(Calendar.SECOND, 59);

            if(startTime.getTime().compareTo(endDate.getTime()) > 0) {
                return null;
            }
        }

        return new JobExecution(this, owner, startTime.getTime());
    }

    @Override
    public JobFrequencyType getFrequencyType() {
        return JobFrequencyType.MONTHLY;
    }
}