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
package org.yougi.web.model;

import org.yougi.business.MessageTemplateBean;
import org.yougi.entity.MessageTemplate;
import org.yougi.annotation.ManagedProperty;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
public class MessageTemplateMBean {

    @EJB
    private MessageTemplateBean messageTemplateBean;

    @Inject
    @ManagedProperty("#{param.id}")
    private String id;

    private MessageTemplate messageTemplate;

    public MessageTemplateMBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MessageTemplate getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(MessageTemplate messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public List<MessageTemplate> getMessageTemplates() {
        return messageTemplateBean.findAll();
    }

    @PostConstruct
    public void load() {
        if (id != null && !id.isEmpty()) {
            this.messageTemplate = messageTemplateBean.find(id);
        } else {
            this.messageTemplate = new MessageTemplate();
        }
    }

    public String save() {
        messageTemplateBean.save(this.messageTemplate);
        return "message_templates?faces-redirect=true";
    }

    public String remove() {
        if (messageTemplate != null) {
            messageTemplateBean.remove(messageTemplate.getId());
        }
        return "message_templates?faces-redirect=true";
    }

    public Boolean getExistent() {
        return messageTemplate.getId() != null && !messageTemplate.getId().isEmpty();
    }
}