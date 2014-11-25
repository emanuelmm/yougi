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

import org.junit.Assert;
import org.junit.Test;
import org.yougi.event.entity.Event;

/**
 * @author Ruither 'delki8' Borba - https://github.com/delki8
 */
public class EventMBeanTest {

    private EventMBean eventMBean = new EventMBean();
    
    @Test
    public void testLoadWithNullIdFillingEvent() throws Exception {
        eventMBean.setEvent(null);
        eventMBean.setId(null);
        
        eventMBean.load();
        
        Assert.assertNotNull(eventMBean.getEvent());
    }
    
    @Test
    public void testLoadWithEmptyIdFillingEvent() throws Exception {
        eventMBean.setEvent(null);
        eventMBean.setId("");
        
        eventMBean.load();
        
        Assert.assertNotNull(eventMBean.getEvent());
    }
    
    @Test
    public void testLoadWithFilledIdKeepingOldEvent() throws Exception {
        Event event = new Event("1");
        eventMBean.setEvent(event);
        eventMBean.setId("1");
        
        eventMBean.load();
        
        Assert.assertEquals(event, eventMBean.getEvent());
    }
    
}
