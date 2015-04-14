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
package org.yougi.util;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Felipe W. M. Martins - https://github.com/felipewmartins
 */
public class WebTextUtilsTest {
  
  private String paragraph;
  private Date date;
  private SimpleDateFormat sdf;
  
  @Before
  public void initialize(){
    paragraph = "<p>This is a Test</p>";
    date = new Date();
    sdf = new SimpleDateFormat("yyyy-MM-dd");
  }
  
    @Test
    public void testConvertLineBreakToHTMLParagraph() throws Exception {
      Assert.assertEquals(paragraph, WebTextUtils.convertLineBreakToHTMLParagraph("This is a Test"));

    }

    @Test
    public void testGetFormattedDate() throws Exception {

    }

    @Test
    public void testGetFormattedTime() throws Exception {

    }

    @Test
    public void testGetFormattedDateTime() throws Exception {

    }

    @Test
    public void testPrintAddress() throws Exception {

    }
}
