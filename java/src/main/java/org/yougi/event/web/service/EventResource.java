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
package org.yougi.event.web.service;

import org.yougi.event.business.EventBean;
import org.yougi.event.entity.Event;

import javax.annotation.ManagedBean;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Represents a country covered by the Java User Group.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Path("events")
@ManagedBean
public class EventResource {

    @EJB
    private EventBean eventBean;

    @GET
    @RolesAllowed("member")
    @Produces("application/xml")
    public List<Event> getAll() {
        return eventBean.findUpCommingEvents();
    }

    @GET
    @Path("{id}")
    @Produces("application/xml")
    public Event getEvent(@PathParam("id") String id) {
        Event event = eventBean.find(id);
        if(event == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return event;
    }
}