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
package org.yougi.web.listener;

import org.yougi.business.UserSessionBean;
import org.yougi.entity.UserSession;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@WebListener
public class UserSessionListener implements HttpSessionListener {

    private static final Logger LOGGER = Logger.getLogger(UserSessionListener.class.getSimpleName());

    @EJB
    private UserSessionBean userSessionBean;

    @Inject
    private HttpServletRequest request;

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        LOGGER.log(Level.INFO, "Session id:" + session.getId());

        UserSession userSession = new UserSession();
        userSession.setSessionId(session.getId());
        userSession.setIpAddress(request.getRemoteAddr());
        userSession.setStart(Calendar.getInstance().getTime());
        userSessionBean.save(userSession);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        UserSession userSession = userSessionBean.findBySessionId(session.getId());
        userSession.setEnd(Calendar.getInstance().getTime());
        userSessionBean.save(userSession);
    }
}