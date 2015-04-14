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
package org.yougi.business;

import com.mysema.query.jpa.impl.JPAQuery;
import org.yougi.entity.QUserSession;
import org.yougi.entity.UserAccount;
import org.yougi.entity.UserSession;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class UserSessionBean extends AbstractBean<UserSession> {
    private static final Logger LOGGER = Logger.getLogger(UserSessionBean.class.getSimpleName());

    @PersistenceContext
    private EntityManager em;

    public UserSessionBean() {
        super(UserSession.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserSession findBySessionId(String sessionId) {
        List<UserSession> userSessions = em.createQuery("select us from UserSession us where us.sessionId = :sessionId order by us.start desc", UserSession.class)
                                           .setParameter("sessionId", sessionId)
                                           .getResultList();

        if (userSessions.isEmpty()) {
            return null;
        }
        else {
            return userSessions.get(0);
        }
    }

    public List<UserSession> findByUserAccount(UserAccount userAccount) {
        return em.createQuery("select us from UserSession us where us.userAccount = :userAccount order by us.start desc", UserSession.class)
                 .setParameter("userAccount", userAccount)
                 .getResultList();
    }

    public List<UserSession> findActiveSessions() {
        JPAQuery query = new JPAQuery(em);
        QUserSession us = QUserSession.userSession;

        return query.from(us).where(us.userAccount.isNotNull(), us.end.isNull()).list(us);
    }

    public void cleanFinishedAnonimousSessions() {
        em.createQuery("delete from UserSession us where us.end is not null and us.userAccount is null").executeUpdate();
    }
}