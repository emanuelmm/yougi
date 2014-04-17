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
package org.cejug.yougi.entity;

import org.cejug.yougi.exception.BusinessLogicException;

import javax.persistence.*;
import java.util.Calendar;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@DiscriminatorValue("MONTHLY")
public class JobMonthlyScheduler extends JobScheduler {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "day_month")
    private Integer dayOfTheMonth;

    /**
     * The day of the month in which the job runs.
     * */
    public Integer getDayOfTheMonth() {
        return dayOfTheMonth;
    }

    public void setDayOfTheMonth(Integer dayOfTheMonth) {
        this.dayOfTheMonth = dayOfTheMonth;
    }

    @Override
    public JobExecution getNextJobExecution(UserAccount owner) throws BusinessLogicException {
        Calendar today = Calendar.getInstance();

        checkInterval(today);

        // Calculate start time
        Calendar startTime = initializeStartTime();

        while(today.compareTo(startTime) > 0) {
            startTime.add(Calendar.MONTH, this.getFrequency());
        }

        while(startTime.get(Calendar.DAY_OF_MONTH) != this.getDayOfTheMonth()) {
            startTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        Calendar time = Calendar.getInstance();
        time.setTime(this.getStartTime());
        startTime.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        startTime.set(Calendar.MINUTE, time.get(Calendar.MINUTE));

        JobExecution jobExecution = new JobExecution(this, owner);

        jobExecution.setStartTime(startTime);

        return jobExecution;
    }

    @Override
    public JobFrequencyType getFrequencyType() {
        return JobFrequencyType.MONTHLY;
    }
}