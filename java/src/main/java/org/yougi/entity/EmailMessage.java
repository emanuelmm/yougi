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

import org.yougi.reference.EmailMessageFormat;
import org.yougi.reference.StorageDuration;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Adapts a mime email message to the application domain, considering a
 * UserAccount as a usual recipient.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
public abstract class EmailMessage implements Serializable, Cloneable {

    public static final String CHARSET = "UTF-8";
    private static final Logger LOGGER = Logger.getLogger(EmailMessage.class.getSimpleName());

    private List<UserAccount> recipients;
    private List<UserAccount> recipientsCopy;
    private List<UserAccount> recipientsHiddenCopy;
    private String sender;
    private String subject;
    private String body;
    private EmailMessageFormat format;
    private String reference;
    private StorageDuration storageDuration;

    public static EmailMessage getInstance(EmailMessageFormat format) {
        EmailMessage messageEmail;
        switch (format) {
            case TEXT_PLAIN:
                messageEmail = new EmailMessagePlain();
                break;
            case TEXT_HTML:
                messageEmail = new EmailMessageHtml();
                break;
            case TEXT_MIXED:
                messageEmail = new EmailMessageMixed();
                break;
            default:
                messageEmail = new EmailMessagePlain();
        }
        return messageEmail;
    }

    public static EmailMessage getInstance(EmailMessage emailMessage) {
        EmailMessage newMessage = getInstance(emailMessage.getFormat());

        if (newMessage != null) {
            newMessage.setRecipients(emailMessage.getRecipients());
            newMessage.setRecipientsCopy(emailMessage.getRecipientsCopy());
            newMessage.setRecipientsHiddenCopy(emailMessage.getRecipientsHiddenCopy());
            newMessage.setSender(emailMessage.getSender());
            newMessage.setSubject(emailMessage.getSubject());
            newMessage.setBody(emailMessage.getBody());
            newMessage.setReference(emailMessage.getReference());
            newMessage.setStorageDuration(emailMessage.getStorageDuration());
        }

        return newMessage;
    }

    public final UserAccount getRecipient() {
        if (recipients != null && !recipients.isEmpty()) {
            return recipients.get(0);
        }
        return null;
    }

    public List<UserAccount> getRecipients() {
        return recipients;
    }

    public final javax.mail.Address[] getRecipientsAddresses() throws AddressException {
        javax.mail.Address[] localRecipients = null;
        if (this.recipients != null) {
            localRecipients = new javax.mail.Address[this.recipients.size()];
            for (int i = 0; i < this.recipients.size(); i++) {
                localRecipients[i] = new InternetAddress(this.recipients.get(i).getEmail());
            }
        }
        return localRecipients;
    }

    public final void setRecipient(UserAccount recipient) {
        if (recipients == null) {
            recipients = new ArrayList<>();
            recipients.add(recipient);
        } else {
            recipients.set(0, recipient);
        }
    }

    public final void setRecipients(List<UserAccount> recipients) {
        this.recipients = recipients;
    }

    public final void addRecipient(UserAccount recipient) {
        if (recipients == null) {
            recipients = new ArrayList<>();
        }
        recipients.add(recipient);
    }

    public final UserAccount getRecipientCopy() {
        if (recipientsCopy != null && !recipientsCopy.isEmpty()) {
            return recipientsCopy.get(0);
        }
        return null;
    }

    public List<UserAccount> getRecipientsCopy() {
        return recipientsCopy;
    }

    public final javax.mail.Address[] getRecipientsCopyAddresses() throws AddressException {
        javax.mail.Address[] localRecipients = null;
        if (this.recipientsCopy != null) {
            localRecipients = new javax.mail.Address[this.recipientsCopy.size()];
            for (int i = 0; i < this.recipientsCopy.size(); i++) {
                LOGGER.log(Level.INFO, "Recipient: {0}", this.recipientsCopy.get(i));
                localRecipients[i] = new InternetAddress(this.recipientsCopy.get(i).getEmail());
            }
        }
        return localRecipients;
    }

    public final void setRecipientCopy(UserAccount recipientCopy) {
        if (recipientsCopy == null) {
            recipientsCopy = new ArrayList<>();
            recipientsCopy.add(recipientCopy);
        } else {
            recipientsCopy.set(0, recipientCopy);
        }
    }

    public final void setRecipientsCopy(List<UserAccount> recipientsCopy) {
        this.recipientsCopy = recipientsCopy;
    }

    public final void addRecipientCopy(UserAccount recipientCopy) {
        if (recipientsCopy == null) {
            recipientsCopy = new ArrayList<>();
        }
        recipientsCopy.add(recipientCopy);
    }

    public final UserAccount getRecipientHiddenCopy() {
        if (recipientsHiddenCopy != null && !recipientsHiddenCopy.isEmpty()) {
            return recipientsHiddenCopy.get(0);
        }
        return null;
    }

    public List<UserAccount> getRecipientsHiddenCopy() {
        return recipientsHiddenCopy;
    }

    public final javax.mail.Address[] getRecipientsHiddenCopyAddresses() throws AddressException {
        javax.mail.Address[] jRecipients = null;
        if (this.recipientsHiddenCopy != null) {
            jRecipients = new javax.mail.Address[this.recipientsHiddenCopy.size()];
            for (int i = 0; i < this.recipientsHiddenCopy.size(); i++) {
                jRecipients[i] = new InternetAddress(this.recipientsHiddenCopy.get(i).getEmail());
            }
        }
        return jRecipients;
    }

    public final void setRecipientHiddenCopy(UserAccount recipientHiddenCopy) {
        if (this.recipientsHiddenCopy == null) {
            this.recipientsHiddenCopy = new ArrayList<>();
            this.recipientsHiddenCopy.add(recipientHiddenCopy);
        } else {
            this.recipientsHiddenCopy.set(0, recipientHiddenCopy);
        }
    }

    public final void setRecipientsHiddenCopy(List<UserAccount> recipientHiddenCopy) {
        this.recipientsHiddenCopy = recipientHiddenCopy;
    }

    public final void addRecipientHiddenCopy(UserAccount recipientHiddenCopy) {
        if (this.recipientsHiddenCopy == null) {
            this.recipientsHiddenCopy = new ArrayList<>();
        }
        this.recipientsHiddenCopy.add(recipientHiddenCopy);
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public final String getSubject() {
        return this.subject;
    }

    public final void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return this.body;
    }

    public String getHtmlBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public EmailMessageFormat getFormat() {
        return format;
    }

    protected void setFormat(EmailMessageFormat format) {
        this.format = format;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public StorageDuration getStorageDuration() {
        return storageDuration;
    }

    public void setStorageDuration(StorageDuration storageDuration) {
        this.storageDuration = storageDuration;
    }

    public abstract MimeMessage createMessage(Session mailSession) throws MessagingException;

    protected MimeMessage createMimeMessage(Session mailSession) throws MessagingException {
        MimeMessage messageMime = new MimeMessage(mailSession);
        messageMime.setSubject(this.getSubject(), CHARSET);
        messageMime.setRecipients(Message.RecipientType.TO, getRecipientsAddresses());
        messageMime.setRecipients(Message.RecipientType.CC, getRecipientsCopyAddresses());
        messageMime.setRecipients(Message.RecipientType.BCC, getRecipientsHiddenCopyAddresses());
        if (getSender() != null && !getSender().isEmpty()) {
            messageMime.setFrom(new InternetAddress(getSender()));
        }
        return messageMime;
    }

    public List<EmailMessage> recreateMessageByRecipient() {
        List<EmailMessage> messagesEmail = new ArrayList<>();

        EmailMessage messageEmail;
        if (this.recipients != null) {
            for (UserAccount recipient : this.recipients) {
                messageEmail = getInstance(this);
                messageEmail.setRecipients(null);
                messageEmail.setRecipientsCopy(null);
                messageEmail.setRecipientHiddenCopy(null);
                messageEmail.setRecipient(recipient);
                messagesEmail.add(messageEmail);
            }
        }

        if (this.recipientsCopy != null) {
            for (UserAccount recipient : this.recipientsCopy) {
                messageEmail = getInstance(this);
                messageEmail.setRecipients(null);
                messageEmail.setRecipientsCopy(null);
                messageEmail.setRecipientHiddenCopy(null);
                messageEmail.setRecipient(recipient);
                messagesEmail.add(messageEmail);
            }
        }

        if (this.recipientsHiddenCopy != null) {
            for (UserAccount recipient : this.recipientsHiddenCopy) {
                messageEmail = getInstance(this);
                messageEmail.setRecipients(null);
                messageEmail.setRecipientsCopy(null);
                messageEmail.setRecipientHiddenCopy(null);
                messageEmail.setRecipient(recipient);
                messagesEmail.add(messageEmail);
            }
        }
        return messagesEmail;
    }
}