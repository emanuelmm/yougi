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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.yougi.business.AccessGroupBean;
import org.yougi.business.UserGroupBean;
import org.yougi.partnership.business.PartnerBean;
import org.yougi.partnership.entity.Partner;

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
    
}
