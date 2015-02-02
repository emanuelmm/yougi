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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.yougi.entity.City;
import org.yougi.entity.Country;
import org.yougi.entity.Province;
import org.yougi.event.business.EventBean;
import org.yougi.event.business.EventVenueBean;
import org.yougi.event.business.RoomBean;
import org.yougi.event.business.VenueBean;
import org.yougi.event.entity.Event;
import org.yougi.event.entity.Room;
import org.yougi.event.entity.Venue;
import org.yougi.web.model.LocationMBean;

/**
 * @author Ruither 'delki8' Borba - https://github.com/delki8
 */
public class VenueMBeanTest {
    
    @Mock
    private LocationMBean locationMBean;
    
    @Mock
    private VenueBean venueBean;
    
    @Mock
    private RoomBean roomBean;
    
    @Mock
    private EventBean eventBean;
    
    @Mock
    private EventVenueBean eventVenueBean;

    @InjectMocks
    private VenueMBean venueMBean;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testSaveWithSuccessReturn() throws Exception {
        String successReturn = venueMBean.save();
        
        assertEquals("venues?faces-redirect=true", successReturn);
    }
    
    @Test
    public void testSaveCallingSaveOnBean() throws Exception {
        Venue venue = new Venue();
        venueMBean.setVenue(venue);
        
        venueMBean.save();
        
        verify(venueBean).save(venue);
    }
    
    @Test
    public void testSaveSettingCountryFromLocationMBeanOnSavedVenue() throws Exception {
        Venue venue = new Venue();
        venueMBean.setVenue(venue);
        Country country = new Country();
        when(locationMBean.getCountry()).thenReturn(country);
        
        venueMBean.save();
        
        assertEquals(country, venue.getCountry());
    }
    
    @Test
    public void testSaveSettingProvinceFromLocationMBeanOnSavedVenue() throws Exception {
        Venue venue = new Venue();
        venueMBean.setVenue(venue);
        Province province = new Province();
        when(locationMBean.getProvince()).thenReturn(province);
        
        venueMBean.save();
        
        assertEquals(province, venue.getProvince());
    }
    
    @Test
    public void testSaveSettingCityFromLocationMBeanOnSavedVenue() throws Exception {
        Venue venue = new Venue();
        venueMBean.setVenue(venue);
        City city = new City();
        when(locationMBean.getCity()).thenReturn(city);
        
        venueMBean.save();
        
        assertEquals(city, venue.getCity());
    }
    
    @Test
    public void testLoadFillingEventWhenEventIdIsNotNull() throws Exception {
        Event event = new Event("1");
        when(eventBean.find(event.getId())).thenReturn(event);
        venueMBean.setEventId(event.getId());
        
        venueMBean.load();
        
        assertEquals(event, venueMBean.getEvent());
    }

    @Test
    public void testLoadFillingVenueWhenVenueIdIsNotNull() throws Exception {
        Venue venue = new Venue("1");
        when(venueBean.find(venue.getId())).thenReturn(venue);
        venueMBean.setId(venue.getId());
        
        venueMBean.load();
        
        assertEquals(venue, venueMBean.getVenue());
    }
    
    @Test
    public void testLoadCallingInitializeOnLocationMBeanWhenVenueIdIsNotNull() throws Exception {
        venueMBean.setId("1");
        when(venueBean.find("1")).thenReturn(new Venue("1").setCountry(new Country()));
        
        venueMBean.load();
        
        verify(locationMBean).initialize();
    }
    
    @Test
    public void testLoadSelectingCountryOnLocationMBeanWithAcronymOfVenuesCountry() throws Exception {
        String acronym = "acro";
        Country country = new Country().setAcronym(acronym);
        Venue venue = new Venue("1").setCountry(country);
        when(venueBean.find(venue.getId())).thenReturn(venue);
        venueMBean.setId(venue.getId());
        
        venueMBean.load();
        
        verify(locationMBean).setSelectedCountry(acronym);
    }
    
    @Test
    public void testLoadSelectingProvinceOnLocationMBeanWithProvinceIdOfVenuesProvince() throws Exception {
        String provinceId = "48";
        Province province = new Province(provinceId);
        Venue venue = new Venue("1").setProvince(province);
        when(venueBean.find(venue.getId())).thenReturn(venue);
        venueMBean.setId(venue.getId());
        
        venueMBean.load();
        
        verify(locationMBean).setSelectedProvince(provinceId);
    }
    
    @Test
    public void testLoadSelectingCityOnLocationMBeanWithCityIdOfVenuesCity() throws Exception {
        String id = "48";
        City city = new City(id);
        Venue venue = new Venue("1").setCity(city);
        when(venueBean.find(venue.getId())).thenReturn(venue);
        venueMBean.setId(venue.getId());
        
        venueMBean.load();
        
        verify(locationMBean).setSelectedCity(id);
    }
    
    @Test
    public void testLoadNotSearchingForAnEventWithEmptyId() throws Exception {
        venueMBean.setEventId("");
        
        venueMBean.load();
        
        verify(eventBean, never()).find(Mockito.anyString());
    }
    
    @Test
    public void testLoadNotSearchingForAnVenueWithEmptyId() throws Exception {
        venueMBean.setId("");
        
        venueMBean.load();
        
        verify(venueBean, never()).find(Mockito.anyString());
    }
    
    @Test
    public void testGetRooms() throws Exception {
        Venue venue = new Venue("248");
        List<Room> rooms = new ArrayList<Room>();
        when(roomBean.findRooms(venue)).thenReturn(rooms);
        venueMBean.setVenue(venue);
        
        venueMBean.getRooms();
        List<Room> roomsOnBean = venueMBean.getRooms();
        
        assertSame(rooms, roomsOnBean);
        verify(roomBean).findRooms(Mockito.any(Venue.class));
    }
    
    @Test
    public void testGetVenues() throws Exception {
        List<Venue> venues = new ArrayList<Venue>();
        when(venueBean.findVenues()).thenReturn(venues);
        
        venueMBean.getVenues();
        List<Venue> venuesOnBean = venueMBean.getVenues();
        
        assertSame(venues, venuesOnBean);
        verify(venueBean).findVenues();
    }
    
    @Test
    public void testGetEvents() throws Exception {
        Venue venue = new Venue("248");
        List<Event> events = new ArrayList<Event>();
        when(eventVenueBean.findEventsVenue(venue)).thenReturn(events);
        venueMBean.setVenue(venue);
        
        venueMBean.getEvents();
        List<Event> eventsOnBean = venueMBean.getEvents();
        
        assertSame(events, eventsOnBean);
        verify(eventVenueBean).findEventsVenue(Mockito.any(Venue.class));
    }
}
