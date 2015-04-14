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
package org.yougi.web.model;

import org.yougi.annotation.UserName;
import org.yougi.business.ApplicationPropertyBean;
import org.yougi.business.TimezoneBean;
import org.yougi.business.UserAccountBean;
import org.yougi.entity.ApplicationProperty;
import org.yougi.entity.Language;
import org.yougi.entity.Timezone;
import org.yougi.entity.UserAccount;
import org.yougi.reference.Properties;
import org.yougi.util.StringUtils;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@SessionScoped
public class UserProfileMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private TimezoneBean timezoneBean;

    @EJB
    private UserAccountBean userAccountBean;

    @EJB
    private ApplicationPropertyBean applicationPropertyBean;

    @Inject
    private FacesContext context;

    @Inject
    @UserName
    private String username;

    private Language language;
    private UserAccount userAccount;
    private String timezone;

    public UserProfileMBean() {
        this.language = Language.getDefaultLanguage();
    }

    public String getLanguage() {
        if (this.language != null) {
            return this.language.getAcronym();
        } else {
            this.language = Language.getDefaultLanguage();
            return this.language.getAcronym();
        }
    }

    public String changeLanguage(Language language) {
        this.language = language;
        context.getViewRoot().setLocale(this.language.getLocale());
        return "index?faces-redirect=true";
    }

    /**
     * In the first invocation, it loads the user account in the session, using
     * the authenticated user to search for the corresponding user in the
     * database. Subsequent invocations return the user account in the session.
     */
    public UserAccount getUserAccount() {
    	if(userAccount == null) {
            this.userAccount = userAccountBean.findByUsername(username);
    	}
    	return userAccount;
    }

    /**
     * Returns the time zone of the authenticated user. If no user is
     * authenticated, then it returns the time zone defined in the application
     * properties. If the time zone was not defined in the application
     * properties yet, then it returns the default time zone where the system is
     * running.
     * @return The time zone of the authenticated user.
     */
    public String getTimeZone() {
        if(userAccount != null && userAccount.getTimeZone() != null && !userAccount.getTimeZone().isEmpty()) {
            if(timezone == null) {
                timezone = userAccount.getTimeZone();
            }
            return timezone;
        } else {
            ApplicationProperty appPropTimeZone = applicationPropertyBean.findApplicationProperty(Properties.TIMEZONE);
            if(StringUtils.isNullOrBlank(appPropTimeZone.getPropertyValue())) {
                Timezone tz = timezoneBean.findDefaultTimezone();
                return tz.getId();
            } else {
                return appPropTimeZone.getPropertyValue();
            }
        }
    }

    public Date getWhatTimeIsIt() {
        return Calendar.getInstance().getTime();
    }
}