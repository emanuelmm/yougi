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
package org.yougi.web.controller;

import org.yougi.business.AuthenticationBean;
import org.yougi.business.MessageHistoryBean;
import org.yougi.business.UserAccountBean;
import org.yougi.entity.DeactivationType;
import org.yougi.entity.UserAccount;
import org.yougi.event.business.AttendeeBean;
import org.yougi.event.entity.Event;
import org.yougi.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@SessionScoped
public class MemberMBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(MemberMBean.class.getSimpleName());

    private static final long serialVersionUID = 1L;

    @EJB
    private UserAccountBean userAccountBean;

    @EJB
    private AuthenticationBean authenticationBean;

    @EJB
    private MessageHistoryBean messageHistoryBean;

    @EJB
    private AttendeeBean attendeeBean;

    @Inject
    private LocationMBean locationMBean;

    @Inject
    private FacesContext context;

    private List<UserAccount> userAccounts;

    private List<UserAccount> deactivatedUsers;

    private List<Event> attendedEvents;

    private String userId;

    private UserAccount userAccount;

    private String emailCriteria;

    private String firstLetterCriteria;

    public MemberMBean() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public LocationMBean getLocationMBean() {
        return locationMBean;
    }

    public void setLocationMBean(LocationMBean locationMBean) {
        this.locationMBean = locationMBean;
    }

    public List<UserAccount> getUserAccounts() {
        return this.userAccounts;
    }

    public List<Event> getAttendedEvents() {
        return this.attendedEvents;
    }

    public List<UserAccount> getDeactivatedUserAccounts() {
        if(deactivatedUsers == null) {
            deactivatedUsers = userAccountBean.findAllDeactivatedUserAccounts();
        }
        return deactivatedUsers;
    }

    public String findUserAccountByEmail() {
        if (StringUtils.INSTANCE.isNullOrBlank(this.emailCriteria)) {
            this.userAccounts = userAccountBean.findAllNotVerifiedAccounts();
        } else {
            List<UserAccount> uas = new ArrayList<>(1);
            UserAccount ua = userAccountBean.findByEmail(this.emailCriteria);
            if (ua != null) {
                uas.add(ua);
            }
            this.userAccounts = uas;
        }
        this.firstLetterCriteria = null;
        return "users?faces-redirect=true";
    }

    public String findUserAccountByFirstLetter(String firstLetterCriteria) {
        if (StringUtils.INSTANCE.isNullOrBlank(firstLetterCriteria)) {
            this.userAccounts = userAccountBean.findAllNotVerifiedAccounts();
        } else {
            this.firstLetterCriteria = firstLetterCriteria;
            this.userAccounts = userAccountBean.findAllStartingWith(this.firstLetterCriteria);
            this.emailCriteria = null;
        }

        return "users?faces-redirect=true";
    }

    public String getEmailCriteria() {
        return emailCriteria;
    }

    public void setEmailCriteria(String emailCriteria) {
        this.emailCriteria = emailCriteria;
    }

    public String getFirstLetterCriteria() {
        return firstLetterCriteria;
    }

    public void setFirstLetterCriteria(String firstLetterCriteria) {
        this.firstLetterCriteria = firstLetterCriteria;
    }

    public boolean isConfirmed() {
        if (StringUtils.INSTANCE.isNullOrBlank(userAccount.getConfirmationCode())) {
            return true;
        }
        return false;
    }

    public void validateUserId(FacesContext context, UIComponent toValidate, Object value) {
        String usrId = (String) value;
        if (-1 == usrId.indexOf('@')) {
            throw new ValidatorException(new FacesMessage("Invalid email address."));
        }
    }

    @PostConstruct
    public void load() {
        this.userAccounts = userAccountBean.findAllNotVerifiedAccounts();
    }

    public String load(String userId) {
        this.userId = userId;
        this.userAccount = userAccountBean.find(this.userId);
        this.attendedEvents = attendeeBean.findAttendeedEvents(this.userAccount);

        locationMBean.initialize();

        if (this.userAccount.getCountry() != null) {
            locationMBean.setSelectedCountry(this.userAccount.getCountry().getAcronym());
        }

        if (this.userAccount.getProvince() != null) {
            locationMBean.setSelectedProvince(this.userAccount.getProvince().getId());
        }

        if (this.userAccount.getCity() != null) {
            locationMBean.setSelectedCity(this.userAccount.getCity().getId());
        }

        return "user?faces-redirect=true";
    }

    public String save() {
        save(null);
        return "users?faces-redirect=true";
    }

    /**
     * @param verified if true, the user account if saved with the status of verified.
     */
    private void save(Boolean verified) {
        UserAccount existingUserAccount = userAccountBean.find(userAccount.getId());

        existingUserAccount.setCountry(this.locationMBean.getCountry());
        existingUserAccount.setProvince(this.locationMBean.getProvince());
        existingUserAccount.setCity(this.locationMBean.getCity());
        existingUserAccount.setFirstName(userAccount.getFirstName());
        existingUserAccount.setLastName(userAccount.getLastName());
        existingUserAccount.setGender(userAccount.getGender());
        existingUserAccount.setWebsite(userAccount.getWebsite());
        existingUserAccount.setTwitter(userAccount.getTwitter());
        existingUserAccount.setPublicProfile(userAccount.getPublicProfile());
        existingUserAccount.setMailingList(userAccount.getMailingList());
        existingUserAccount.setNews(userAccount.getNews());
        existingUserAccount.setGeneralOffer(userAccount.getGeneralOffer());
        existingUserAccount.setJobOffer(userAccount.getJobOffer());
        existingUserAccount.setEvent(userAccount.getEvent());
        existingUserAccount.setSponsor(userAccount.getSponsor());
        existingUserAccount.setSpeaker(userAccount.getSpeaker());

        if (verified != null) {
            existingUserAccount.setVerified(verified);
        }

        userAccountBean.save(existingUserAccount);
    }

    public String deactivateMembership() {
        userAccountBean.deactivateMembership(userAccount, DeactivationType.ADMINISTRATIVE);
        return "users?faces-redirect=true";
    }

    public String confirm() {
        try {
            userAccountBean.confirmUser(userAccount.getConfirmationCode());
        } catch (IllegalArgumentException iae) {
            LOGGER.log(Level.INFO, iae.getMessage(), iae);
            context.addMessage(null, new FacesMessage(iae.getMessage()));
            return "user";
        }
        removeSessionScoped();
        return "users?faces-redirect=true";
    }

    private void removeSessionScoped() {
        context.getExternalContext().getSessionMap().remove("memberBean");
    }
}