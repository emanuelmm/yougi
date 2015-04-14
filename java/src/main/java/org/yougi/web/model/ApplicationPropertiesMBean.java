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

import org.yougi.business.ApplicationPropertyBean;
import org.yougi.business.TimezoneBean;
import org.yougi.entity.Timezone;
import org.yougi.reference.Properties;
import org.yougi.util.ResourceBundleHelper;
import org.yougi.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
public class ApplicationPropertiesMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ApplicationPropertyBean applicationPropertyBean;

    @EJB
    private TimezoneBean timezoneBean;

    @Inject
    private FacesContext context;

    @Inject
    private HttpServletRequest request;

    private Map<String, String> applicationProperties;
    private Boolean sendEmails;
    private Boolean receiveEmails;
    private Boolean captchaEnabled;
    private List<Timezone> timezones;

    public Map<String, String> getApplicationProperties() {
        return applicationProperties;
    }

    public void setApplicationProperties(Map<String, String> applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public Boolean getSendEmails() {
        return sendEmails;
    }

    public void setSendEmails(Boolean sendEmails) {
        this.sendEmails = sendEmails;
    }

    public Boolean getReceiveEmails() {
        return receiveEmails;
    }

    public void setReceiveEmails(Boolean receiveEmails) {
        this.receiveEmails = receiveEmails;
    }

    public Boolean getCaptchaEnabled() {
        return captchaEnabled;
    }

    public void setCaptchaEnabled(Boolean captchaEnabled) {
        this.captchaEnabled = captchaEnabled;
    }

    public List<Timezone> getTimezones() {
        if(this.timezones == null) {
            this.timezones = timezoneBean.findTimezones();
        }
        return this.timezones;
    }

    @PostConstruct
    public void load() {
        applicationProperties = applicationPropertyBean.findApplicationProperties();

        if ("".equals(applicationProperties.get(Properties.URL.getKey()))) {
            applicationProperties.put(Properties.URL.getKey(), getUrl());
        }

        if ("true".equals(applicationProperties.get(Properties.SEND_EMAILS.getKey()))) {
            sendEmails = true;
        }

        if ("true".equals(applicationProperties.get(Properties.RECEIVE_EMAILS.getKey()))) {
            receiveEmails = true;
        }

        if ("true".equals(applicationProperties.get(Properties.CAPTCHA_ENABLED.getKey()))) {
            captchaEnabled = true;
        }

        String timezoneValue = applicationProperties.get(Properties.TIMEZONE.getKey());
        if(StringUtils.isNullOrBlank(timezoneValue)) {
            Timezone timezone = timezoneBean.findDefaultTimezone();
            applicationProperties.put(Properties.TIMEZONE.getKey(), timezone.getId());
        }
    }

    public String save() {
        this.applicationProperties.put(Properties.SEND_EMAILS.getKey(), sendEmails.toString());
        this.applicationProperties.put(Properties.RECEIVE_EMAILS.getKey(), receiveEmails.toString());
        this.applicationProperties.put(Properties.CAPTCHA_ENABLED.getKey(), captchaEnabled.toString());
        applicationPropertyBean.save(this.applicationProperties);

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.getMessage("infoPropertiesSaved"), ""));

        return "properties";
    }

    private String getUrl() {
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        return serverName + (serverPort != 80 ? ":" + serverPort : "") + (contextPath.isEmpty() ? "" : contextPath);
    }
}