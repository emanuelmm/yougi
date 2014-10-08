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
package org.yougi.reference;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
public enum StorageDuration {

    /**
     * Less than one day.
     */
    TEMPORARY(0),
    /**
     * One day.
     */
    ONE_DAY(1),
    /**
     * 7 days.
     */
    ONE_WEEK(7),
    /**
     * 30 days.
     */
    ONE_MONTH(30),
    /**
     * 366 days.
     */
    ONE_YEAR(366),
    /**
     * 732 days.
     */
    TWO_YEARS(732),
    /**
     * 1098 days.
     */
    THREE_YEARS(1098);

    private Integer days;

    StorageDuration(Integer days) {
        this.days = days;
    }

    public Integer getDays() {
        return this.days;
    }
}