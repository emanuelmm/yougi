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
package org.yougi.annotation.producer;

import org.yougi.business.UserAccountBean;
import org.yougi.business.UserSessionBean;
import org.yougi.entity.UserAccount;
import org.yougi.entity.UserSession;
import org.yougi.annotation.UserName;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * @author Daniel Cunha - danielsoro@gmail.com
 */
@SessionScoped
public class UserNameProducer implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(UserNameProducer.class.getSimpleName());


    @EJB
    private UserAccountBean userAccountBean;

    @EJB
    private UserSessionBean userSessionBean;

    @Inject
    private FacesContext facesContext;

    @Inject
    private HttpServletRequest httpServletRequest;

    private UserAccount userAccount;

    @Produces @Named @UserName
    public String getUserName() {
        loadUserAccount();
        return userAccount == null ? null : userAccount.getEmail();
    }

    @Produces @Named
    public String getFirstName() {
        loadUserAccount();
        return userAccount == null ? "" : userAccount.getFirstName();
    }

    private UserAccount loadUserAccount() {
        if(userAccount == null) {
            String userName = httpServletRequest.getRemoteUser();
            userAccount = userAccountBean.findByUsername(userName);
            if(userAccount != null) {
                String sessionId = facesContext.getExternalContext().getSessionId(false);
                UserSession userSession = userSessionBean.findBySessionId(sessionId);
                userSession.setUserAccount(userAccount);
                userSessionBean.save(userSession);
                userSessionBean.cleanFinishedAnonimousSessions();
            }
        }
        return userAccount;
    }
}
