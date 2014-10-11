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
package org.yougi.entity;

import org.yougi.exception.EnvironmentResourceException;
import org.yougi.reference.ContentType;
import org.yougi.reference.EmailMessageFormat;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
public class EmailMessagePlain extends EmailMessage {

    private static final Logger LOGGER = Logger.getLogger(EmailMessagePlain.class.getSimpleName());

    protected EmailMessagePlain() {
        setFormat(EmailMessageFormat.TEXT_PLAIN);
    }

    @Override
    public MimeMessage createMessage(Session mailSession) throws EnvironmentResourceException {
        try {
            MimeMessage msg = createMimeMessage(mailSession);
            msg.setText(getBody(), CHARSET);
            msg.setHeader("Content-Type", ContentType.TEXT_PLAIN.toString() + ";charset=" + CHARSET);
            return msg;
        } catch (MessagingException me) {
            throw new EnvironmentResourceException("Error when creating the email message.", me);
        }
    }
}