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

import org.yougi.util.EntitySupport;
import org.yougi.entity.Identified;

import javax.persistence.EntityManager;

/**
 * Implements basic operations
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 * @param <T> Any entity class that implements Identified.
 */
public abstract class AbstractBean<T extends Identified> {

    private final Class<T> entityClass;

    public AbstractBean(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    /**
     * Save an entity instance on the database. The Id of the entity should
     * support a UUID string because this method will set the id as a UUID
     * string if the id is not defined yet.
     * @param entity Any entity class that implements Identified.
     */
    public T save(T entity) {
        T persistedEntity;
        if(EntitySupport.isIdNotValid(entity)) {
            entity.setId(EntitySupport.generateEntityId());
            getEntityManager().persist(entity);
            persistedEntity = entity;
        } else {
            persistedEntity = update(entity);
        }
        return persistedEntity;
    }

    /**
     * Update an entity instance on the database. It only works if the entity
     * has a valid Id, otherwise it returns null. In general the method save()
     * already fulfills the need for updating an entity, however sometimes the
     * method save is overwritten, losing its original behavior. The update()
     * method helps to cover those exceptions.
     * @param entity Any entity class that implements Identified.
     * */
    public T update(T entity) {
        T persistedEntity = null;
        if(EntitySupport.isIdValid(entity.getId())) {
            persistedEntity = getEntityManager().merge(entity);
        }
        return persistedEntity;
    }

    /**
     * Remove an entity instance from the database. It's enough to provide the
     * Id of the entity to be removed.
     * @param id the id of a persisted entity.
     * */
    public void remove(String id) {
        getEntityManager().remove(find(id));
    }

    /**
     * Find a particular entity on the database by its Id.
     * @param id the id of a persisted entity.
     * @return a persisted entity correspondent to the informed id. Null if there is
     * no entity with the informed id.
     * */
    public T find(String id) {
        return getEntityManager().find(this.entityClass, id);
    }
}