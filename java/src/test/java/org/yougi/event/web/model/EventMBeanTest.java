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
package org.yougi.event.web.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.yougi.business.UserAccountBean;
import org.yougi.entity.UserAccount;
import org.yougi.event.business.AttendeeBean;
import org.yougi.event.business.EventBean;
import org.yougi.event.entity.Attendee;
import org.yougi.event.entity.Event;

/**
 * @author Ruither 'delki8' Borba - https://github.com/delki8
 */
public class EventMBeanTest {

    @Mock
    private EventBean eventBean;
    @Mock
    private UserAccountBean userAccountBean;
    @Mock
    private AttendeeBean attendeeBean;

    @InjectMocks
    private EventMBean eventMBean;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoadWithNullIdInstantiatingNewEvent() throws Exception {
        eventMBean.setEvent(null);
        eventMBean.setId(null);

        eventMBean.load();

        Assert.assertNotNull(eventMBean.getEvent());
    }

    @Test
    public void testLoadWithEmptyIdInstantiatingNewEvent() throws Exception {
        eventMBean.setEvent(null);
        eventMBean.setId("");

        eventMBean.load();

        Assert.assertNotNull(eventMBean.getEvent());
    }

    @Test
    public void testLoadWithFilledIdKeepingOldEvent() throws Exception {
        Event event = createDataToLoadTest();

        eventMBean.load();

        Assert.assertEquals(event, eventMBean.getEvent());
    }

    @Test
    public void testLoadWithFilledIdSettingAttendee() throws Exception {
        createDataToLoadTest();

        eventMBean.load();

        Assert.assertTrue(eventMBean.getIsAttending());
    }

    @Test
    public void testLoadWithFilledIdSettingAttendingNumber() throws Exception {
        createDataToLoadTest();

        eventMBean.load();

        Assert.assertEquals(new Long(123), eventMBean.getNumberPeopleAttending());
    }

    @Test
    public void testLoadWithFilledIdSettingAttendedNumber() throws Exception {
        createDataToLoadTest();

        eventMBean.load();

        Assert.assertEquals(new Long(658), eventMBean.getNumberPeopleAttended());
    }

    @Test
    public void testLoadWithFilledIdSettingSelectedParentWithIdOfEventsParent() throws Exception {
        String parentsId = "6";
        Event parentEvent = new Event(); 
        parentEvent.setId(parentsId);
        Event event = createDataToLoadTest();
        event.setParent(parentEvent);

        eventMBean.load();

        Assert.assertEquals(parentsId, eventMBean.getSelectedParent());
    }

    @Test
    public void testSaveWithSuccessReturnWithNullParent() throws Exception {
        String mappingReturn = eventMBean.save();

        assertEquals("events?faces-redirect=true", mappingReturn);
    }

    @Test
    public void testSaveWithSuccessReturnWithEmptyParent() throws Exception {
        eventMBean.setSelectedParent("");

        String mappingReturn = eventMBean.save();

        assertEquals("events?faces-redirect=true", mappingReturn);
    }

    @Test
    public void testSaveInstantiatingNewEventWithSettedParent() throws Exception {
        String parentsId = "6";
        Event event = new Event();
        eventMBean.setSelectedParent(parentsId);
        eventMBean.setEvent(event);

        eventMBean.save();

        Event parentEvent = new Event();
        parentEvent.setId(parentsId);
        assertEquals(parentEvent, event.getParent());
    }

    @Test
    public void testSavePersistingEntity() throws Exception {
        Event event = new Event();
        event.setId("1");
        eventMBean.setEvent(event);

        eventMBean.save();

        verify(eventBean).save(event);
    }
    
    @Test
    public void testRemoveWithSuccessReturn() throws Exception {
        eventMBean.setEvent(new Event());
        
        String successReturn = eventMBean.remove();
        
        assertEquals("events?faces-redirect=true", successReturn);
    }
    
    @Test
    public void testRemoveCallingRemoveOnBean() throws Exception {
        Event event = new Event("1");
        eventMBean.setEvent(event);
        
        eventMBean.remove();
        
        verify(eventBean).remove("1");
    }

    private Event createDataToLoadTest() {
        eventMBean.setId("1");
        Event event = new Event("1");
        UserAccount userAccount = new UserAccount("52");
        Attendee attendee = new Attendee("32");
        when(eventBean.find("1")).thenReturn(event);
        when(userAccountBean.findByUsername(null)).thenReturn(userAccount);
        when(attendeeBean.find(event, userAccount)).thenReturn(attendee);
        when(attendeeBean.findNumberPeopleAttending(event)).thenReturn(new Long(123));
        when(attendeeBean.findNumberPeopleAttended(event)).thenReturn(new Long(658));
        return event;
    }

}
