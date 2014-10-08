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

import org.yougi.exception.BusinessLogicException;
import org.yougi.reference.EmailMessageFormat;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
public class EmailMessageHtml extends EmailMessage {

    private static final Logger LOGGER = Logger.getLogger(EmailMessageHtml.class.getSimpleName());

    protected EmailMessageHtml() {
        super.setFormat(EmailMessageFormat.TEXT_HTML);
    }

    @Override
    public MimeMessage createMessage(Session mailSession) throws BusinessLogicException {
        try {
            MimeMessage msg = createMimeMessage(mailSession);

            StringBuilder logMessage = new StringBuilder();
            logMessage.append("ServiceEmail: Mime message html");

            if (super.getAttachments() == null || super.getAttachments().isEmpty()) {
                msg.setText(super.getHtmlBody(), CHARSET);
                msg.setHeader("Content-Type", ContentType.TEXT_HTML.toString() + ";charset=" + CHARSET);
                logMessage.append(" sans pieces jointes");
            } else {
                // Multipart mixte.
                Multipart multipart = new MimeMultipart();

                MimeBodyPart messageHtml = new MimeBodyPart();
                messageHtml.setContent(super.getBody(), ContentType.TEXT_HTML.toString() + ";charset=" + CHARSET);
                multipart.addBodyPart(messageHtml);

                addAttachment(multipart);

                msg.setContent(multipart);
                logMessage.append(" avec pieces jointes");
            }
            logMessage.append(" a été créé.");
            LOGGER.log(Level.INFO, logMessage.toString());
            return msg;
        } catch (MessagingException me) {
            throw new BusinessLogicException("Error lors de la création du message électronique.", me);
        }
    }
}