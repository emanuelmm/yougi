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

import org.yougi.entity.Community;

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
public class CommunityBean extends AbstractBean<Community> {

    private static final Logger LOGGER = Logger.getLogger(CommunityBean.class.getSimpleName());

    @PersistenceContext
    private EntityManager em;

    public CommunityBean() {
        super(Community.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Community> findAll() {
        return em.createQuery("select c from Community c order by c.name asc", Community.class)
                 .getResultList();
    }

    public Community findMainCommunity() {
        List<Community> communities = em.createQuery("select c from Community c order by c.name asc", Community.class).getResultList();
        if(!communities.isEmpty()) {
            return communities.get(0);
        }
        return null;
    }

    public boolean hasMultipleCommunities() {
        Long count = em.createQuery("select count(*) from Community", Long.class).getSingleResult();
        return count > 1L;
    }
}
