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
package org.yougi.partnership.web.model;

import static org.junit.Assert.*;
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
import org.yougi.business.AccessGroupBean;
import org.yougi.business.UserGroupBean;
import org.yougi.entity.AccessGroup;
import org.yougi.entity.Address;
import org.yougi.entity.City;
import org.yougi.entity.Country;
import org.yougi.entity.Province;
import org.yougi.entity.UserAccount;
import org.yougi.partnership.business.PartnerBean;
import org.yougi.partnership.business.RepresentativeBean;
import org.yougi.partnership.entity.Partner;
import org.yougi.web.model.LocationMBean;

/**
 * @author Ruither 'delki8' Borba - https://github.com/delki8
 */
public class PartnerMBeanTest {

    @Mock
    private UserGroupBean userGroupBean;

    @Mock
    private AccessGroupBean accessGroupBean;

    @Mock
    private PartnerBean partnerBean;

    @Mock
    private LocationMBean locationMBean;

    @Mock
    private RepresentativeBean representativeBean;

    @InjectMocks
    private PartnerMBean partnerMBean;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRemoveWithSuccessReturn() throws Exception {
        partnerMBean.setPartner(new Partner());

        String successReturn = partnerMBean.remove();

        assertEquals("partners?faces-redirect=true", successReturn);
    }

    @Test
    public void testRemoveCallignRemoveOnBeanPassingPartnersId() throws Exception {
        String partnersId = "1";
        partnerMBean.setPartner(new Partner(partnersId));

        partnerMBean.remove();

        verify(partnerBean).remove(partnersId);
    }

    @Test
    public void testLoadWithNullIdInstantiatingNewPartnerWithAddress() throws Exception {
        partnerMBean.setId(null);

        partnerMBean.load();

        assertNotNull(partnerMBean.getPartner());
        assertNotNull(partnerMBean.getPartner().getAddress());
    }
    
    @Test
    public void testLoadWithEmptyIdEvaluatingCadidatesSource() throws Exception {
        partnerMBean.setId("");
        AccessGroup accessGroup = new AccessGroup("partners", "parceiros");
        when(accessGroupBean.findAccessGroupByName("partners")).thenReturn(accessGroup);
        List<UserAccount> usersGroup = new ArrayList<UserAccount>();
        when(userGroupBean.findUsersGroup(accessGroup)).thenReturn(usersGroup);

        partnerMBean.load();

        assertSame(usersGroup, partnerMBean.getCandidates().getSource());
    }
    
    @Test
    public void testLoadWithEmptyIdEvaluatingCadidatesTarget() throws Exception {
        partnerMBean.setId("");
        AccessGroup accessGroup = new AccessGroup("partners", "parceiros");
        when(accessGroupBean.findAccessGroupByName("partners")).thenReturn(accessGroup);
        List<UserAccount> usersGroup = new ArrayList<UserAccount>();
        when(userGroupBean.findUsersGroup(accessGroup)).thenReturn(usersGroup);

        List<UserAccount> representativePersons = new ArrayList<UserAccount>();
        representativePersons.add(new UserAccount("167"));

        partnerMBean.load();

        assertTrue(partnerMBean.getCandidates().getTarget().isEmpty());
    }

    @Test
    public void testLoadWithFilledId() throws Exception {
        partnerMBean.setId("248");
        Partner partner = new Partner("248").setAddress(new Address());
        when(partnerBean.find("248")).thenReturn(partner);

        partnerMBean.load();

        assertEquals(partner, partnerMBean.getPartner());
    }

    @Test
    public void testLoadWithFilledIdSelectingCountryOnLocationMBean() throws Exception {
        partnerMBean.setId("248");
        Country country = new Country("Brasil");
        when(partnerBean.find("248")).thenReturn(new Partner("248").setAddress(new Address().setCountry(country)));

        partnerMBean.load();

        Mockito.verify(locationMBean).setSelectedCountry(country.getAcronym());
    }

    @Test
    public void testLoadWithFilledIdSelectingProvinceOnLocationMBean() throws Exception {
        partnerMBean.setId("248");
        Province province = new Province("Goias");
        when(partnerBean.find("248")).thenReturn(new Partner("248").setAddress(new Address().setProvince(province)));

        partnerMBean.load();

        Mockito.verify(locationMBean).setSelectedProvince(province.getId());
    }

    @Test
    public void testLoadWithFilledIdSelectingCityOnLocationMBean() throws Exception {
        partnerMBean.setId("248");
        City city = new City("Goiania");
        when(partnerBean.find("248")).thenReturn(new Partner("248").setAddress(new Address().setCity(city)));

        partnerMBean.load();

        Mockito.verify(locationMBean).setSelectedCity(city.getId());
    }

    @Test
    public void testLoadWithFilledIdEvaluatingCandidatesSource() throws Exception {
        partnerMBean.setId("248");
        when(partnerBean.find("248")).thenReturn(new Partner("248").setAddress(new Address()));
        AccessGroup accessGroup = new AccessGroup("partners", "parceiros");
        when(accessGroupBean.findAccessGroupByName("partners")).thenReturn(accessGroup);
        List<UserAccount> usersGroup = new ArrayList<UserAccount>();
        when(userGroupBean.findUsersGroup(accessGroup)).thenReturn(usersGroup);

        partnerMBean.load();

        assertSame(usersGroup, partnerMBean.getCandidates().getSource());
    }

    @Test
    public void testLoadWithFilledIdEvaluatingCandidatesTarget() throws Exception {
        partnerMBean.setId("248");
        Partner partner = new Partner("248").setAddress(new Address());
        when(partnerBean.find("248")).thenReturn(partner);

        AccessGroup accessGroup = new AccessGroup("partners", "parceiros");
        when(accessGroupBean.findAccessGroupByName("partners")).thenReturn(accessGroup);
        List<UserAccount> usersGroup = new ArrayList<UserAccount>();
        when(userGroupBean.findUsersGroup(accessGroup)).thenReturn(usersGroup);

        List<UserAccount> representativePersons = new ArrayList<UserAccount>();
        representativePersons.add(new UserAccount("167"));
        when(representativeBean.findRepresentativePersons(partner)).thenReturn(representativePersons);

        partnerMBean.load();

        assertEquals(representativePersons, partnerMBean.getCandidates().getTarget());
    }
    
    @Test
    public void testLoadWithFilledIdRemovingRepresentativePersonsFromUsersGroups() throws Exception {
        partnerMBean.setId("248");
        Partner partner = new Partner("248").setAddress(new Address());
        when(partnerBean.find("248")).thenReturn(partner);

        UserAccount userAccount = new UserAccount("167");
        AccessGroup accessGroup = new AccessGroup("partners", "parceiros");
        when(accessGroupBean.findAccessGroupByName("partners")).thenReturn(accessGroup);
        List<UserAccount> usersGroup = new ArrayList<UserAccount>();
        when(userGroupBean.findUsersGroup(accessGroup)).thenReturn(usersGroup);
        usersGroup.add(userAccount);

        List<UserAccount> representativePersons = new ArrayList<UserAccount>();
        representativePersons.add(userAccount);
        when(representativeBean.findRepresentativePersons(partner)).thenReturn(representativePersons);

        partnerMBean.load();

        assertEquals(userAccount, partnerMBean.getCandidates().getTarget().get(0));
        assertEquals(1, partnerMBean.getCandidates().getTarget().size());
        assertTrue(partnerMBean.getCandidates().getSource().isEmpty());
    }
    
}
