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
package org.yougi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
public class DateTimeUtils {

    private DateTimeUtils() {}

    public static String getFormattedDate(Date date, String formatDate) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
        return sdf.format(date);
    }

    public static String getFormattedTime(Date time, String formatTime, String timezone) {
        if (time == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(formatTime);
        TimeZone tz = TimeZone.getTimeZone(timezone);
        sdf.setTimeZone(tz);
        return sdf.format(time);
    }

    public static String getFormattedDateTime(Date dateTime, String formatDateTime, String timezone) {
        if (dateTime == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(formatDateTime);
        TimeZone tz = TimeZone.getTimeZone(timezone);
        sdf.setTimeZone(tz);
        return sdf.format(dateTime);
    }

    /**
     * @param date A date.
     * @param dateFormat The expected format of the date.
     * @return an instance of date equivalent to the informed string.
     * */
    public static Date getDate(String date, String dateFormat) throws ParseException {
        return new SimpleDateFormat(dateFormat).parse(date);
    }

    /**
     * If date and time are treated separately, this method take both and return an instance of Date with the date and
     * time defined as informed.
     * @param date A date.
     * @param dateFormat The expected format of the date.
     * @param time A time.
     * @param timeFormat The expected format of the time.
     * @return an instance of date equivalent to the informed date and time.
     * */
    public static Date getDateAndTime(String date, String dateFormat, String time, String timeFormat) throws ParseException {
        SimpleDateFormat formatDate = new SimpleDateFormat(dateFormat);
        SimpleDateFormat formatTime = new SimpleDateFormat(timeFormat);

        return mergeDateAndTime(formatDate.parse(date), formatTime.parse(time));
    }

    /**
     * If date and time are treated separately, this method merge both and return an instance of Date with the date and
     * time defined as informed.
     * @param date A date.
     * @param time A time.
     * @return an instance of date equivalent to the informed date and time. If both parameters are null then the return
     * is also null.
     * */
    public static Date mergeDateAndTime(Date date, Date time) {
        Calendar dateAndTime = Calendar.getInstance();

        if(date != null) {
            dateAndTime.setTime(date);
            if(time != null) {
                Calendar calTime = Calendar.getInstance();
                calTime.setTime(time);
                dateAndTime.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
                dateAndTime.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
            } else {
                dateAndTime.set(Calendar.HOUR_OF_DAY, 0);
                dateAndTime.set(Calendar.MINUTE, 0);
            }
        } else if(time != null) {
            dateAndTime.setTime(time);
        } else {
            return null;
        }

        return dateAndTime.getTime();
    }
}