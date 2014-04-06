package org.cejug.yougi.event.web.controller;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import org.cejug.yougi.event.business.AttendeeBean;
import org.cejug.yougi.event.entity.Attendee;
import org.cejug.yougi.event.entity.Event;
import org.primefaces.context.RequestContext;

@ManagedBean
@SessionScoped
public class RaffleMBean implements Serializable {

    private static final long serialVersionUID = 1L;
	
	@EJB
    private AttendeeBean attendeeBean;
	
	private String eventId;
	
	public void loadAttendees( ){
		RequestContext context = RequestContext.getCurrentInstance();
		List<Attendee> attendees = attendeeBean.findAttendees( new Event(eventId) );
		context.addCallbackParam("attendees",  toJsonString(attendees) );
	}
	
	protected String toJsonString( List<Attendee> attendees ){
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		JsonObjectBuilder builder = Json.createObjectBuilder();
		for (Attendee attendee : attendees) {
			builder.add("name", attendee.getFullName() );
			arrayBuilder.add( builder.build() );
		}
		return arrayBuilder.build().toString();
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	
}
