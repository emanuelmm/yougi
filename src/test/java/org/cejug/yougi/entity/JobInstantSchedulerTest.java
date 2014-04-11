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
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author Hildeberto Mendonca - http://www.hildeberto.com
 */
public class JobInstantSchedulerTest {

    @Test(expected = BusinessLogicException.class)
    public void getNextJobExecutionInFutureDate() throws Exception {
        JobScheduler jobScheduler = new JobInstantScheduler();
        jobScheduler.setDefaultOwner(new UserAccount("user"));
        jobScheduler.setDescription("test");

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, 1);
        jobScheduler.setStartDate(startDate.getTime());
        jobScheduler.getNextJobExecution();
    }

    @Test(expected = BusinessLogicException.class)
    public void getNextJobExecutionAfterEndDate() throws Exception {
        JobScheduler jobScheduler = new JobInstantScheduler();
        jobScheduler.setDefaultOwner(new UserAccount("user"));
        jobScheduler.setDescription("test");

        Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/04/2014");
        jobScheduler.setStartDate(startDate);
        jobScheduler.setStartTime(startDate);

        Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse("05/04/2014");
        jobScheduler.setEndDate(endDate);

        jobScheduler.getNextJobExecution();
    }
}