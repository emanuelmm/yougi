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
package org.cejug.yougi.util;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Felipe W. M. Martins - https://github.com/felipewmartins
 */
public class DateTimeUtilsTest {

    @Test
    public void testGetFormattedDate() throws Exception {
        Date date = new Date();
    	Assert.assertEquals(new SimpleDateFormat("dd-M-yyyy").format(date), DateTimeUtils.INSTANCE.getFormattedDate(date, "dd-M-yyyy"));
    }

    @Test
    public void testGetFormattedTime() throws Exception {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    	Assert.assertEquals(sdf.format(date), DateTimeUtils.INSTANCE.getFormattedTime(date, "HH:mm:ss", "UTC"));
    }

    @Test
    public void testGetFormattedDateTime() throws Exception {
        Date date = new Date();
        SimpleDateFormat sdft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdft.setTimeZone(TimeZone.getTimeZone("GMT-8:00"));
    	Assert.assertEquals(sdft.format(date), DateTimeUtils.INSTANCE.getFormattedTime(date, "yyyy/MM/dd HH:mm:ss", "GMT-8:00"));
    }

    @Test
    public void testGetDate() throws  Exception {
        Date date = DateTimeUtils.INSTANCE.getDate("2014/09/12", "yyyy/MM/dd");
        Assert.assertEquals(date.getTime(), 1410472800000L);
        date = DateTimeUtils.INSTANCE.getDate("12/09/2014", "dd/MM/yyyy");
        Assert.assertEquals(date.getTime(), 1410472800000L);
    }

    @Test
    public void testGetDateAndTime() throws  Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date expectedDate = sdf.parse("2014/09/12 14:30");

        Date date = DateTimeUtils.INSTANCE.getDateAndTime("12/09/2014", "dd/MM/yyyy", "14:30", "HH:mm");
        Assert.assertEquals(date.getTime(), expectedDate.getTime());
    }
}