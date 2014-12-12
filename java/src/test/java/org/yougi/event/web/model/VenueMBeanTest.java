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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.yougi.entity.City;
import org.yougi.entity.Country;
import org.yougi.entity.Province;
import org.yougi.event.business.VenueBean;
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
    
}
