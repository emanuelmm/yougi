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

import org.yougi.business.*;
import org.yougi.entity.*;
import org.yougi.reference.DeactivationType;
import org.yougi.util.ResourceBundleHelper;
import org.yougi.util.StringUtils;
import org.yougi.annotation.ManagedProperty;
import org.yougi.annotation.UserName;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
public class UserAccountMBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UserAccountMBean.class.getSimpleName());

    private static final long serialVersionUID = 1L;

    @EJB
    private UserAccountBean userAccountBean;

    @EJB
    private AuthenticationBean authenticationBean;

    @EJB
    private MessageHistoryBean messageHistoryBean;

    @EJB
    private UserSessionBean userSessionBean;

    @EJB
    private CommunityBean communityBean;

    @EJB
    private CommunityMemberBean communityMemberBean;

    @Inject
    private LocationMBean locationMBean;

    @Inject
    @ManagedProperty("#{param.id}")
    private String id;

    @Inject
    @ManagedProperty("#{param.letter}")
    private String letter;

    @Inject
    private FacesContext context;

    @Inject
    private HttpServletRequest request;

    @Inject
    @UserName
    private String username;

    private String userId;
    private UserAccount userAccount;
    private Authentication authentication;

    private String password;
    private String passwordConfirmation;
    private String validationEmail;
    private String email;

    private Boolean validationPrivacy = false;
    private Boolean hasMultipleCommunities;

    private List<UserAccount> userAccounts;
    private List<UserAccount> unverifiedUserAccounts;
    private List<UserAccount> unconfirmedUserAccounts;
    private List<UserAccount> deactivatedUserAccounts;
    private List<Community> existingCommunities;
    private List<MessageHistory> historicMessages;
    private List<UserSession> userSessions;
    private Map<String, Boolean> selectedCommunities;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * @return the user credentials
     */
    public Authentication getAuthentication() {
        return authentication;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserAccount> getUserAccounts() {
        return this.userAccounts;
    }

    public List<MessageHistory> getHistoricMessages() {
        return this.historicMessages;
    }

    public List<UserSession> getUserSessions() {
        return this.userSessions;
    }

    /**
     * That is not a entity property and it will not be saved in the database.
     * It is used only to check if the user properly typed his password.
     */
    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Boolean> getSelectedCommunities() {
        if(this.selectedCommunities == null) {
            this.selectedCommunities = new HashMap<>();
            this.existingCommunities = getExistingCommunities();

            for(Community community: this.existingCommunities) {
                this.selectedCommunities.put(community.getId(), false);
            }
        }
        return selectedCommunities;
    }

    // Beginning of mail validation
    public void validateEmail(FacesContext context, UIComponent component, Object value) {
        this.validationEmail = (String) value;

        if(userAccountBean.existingAccount(this.validationEmail)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceBundleHelper.getMessage("errorCode0004"), null));
        }
    }

    public void validateEmailConfirmation(FacesContext context, UIComponent component, Object value) {
        String validationEmailConfirmation = (String) value;
        if(!validationEmailConfirmation.equals(this.validationEmail)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleHelper.getMessage("errorCode0003"), null));
        }
    }
    // End of email validation

    // Beginning of password validation
    public void validatePassword(FacesContext context, UIComponent component, Object value) {
        this.password = (String) value;
    }

    public void validatePasswordConfirmation(FacesContext context, UIComponent component, Object value) {
        this.passwordConfirmation = (String) value;
        if(!this.passwordConfirmation.equals(this.password)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleHelper.getMessage("errorCode0005"), null));
        }
    }
    // End of password validation

    // Beginning of privacy composite validation
    public void validatePrivacyOption(FacesContext context, UIComponent component, Object value) {
        if(!this.validationPrivacy) {
            this.validationPrivacy = (Boolean) value;
        }
    }

    public void validatePrivacy(FacesContext context, UIComponent component, Object value) {
        if(!this.validationPrivacy) {
            this.validationPrivacy = (Boolean) value;
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleHelper.getMessage("errorCode0007"), null));
        }
    }
    // End of privacy composite validation

    public LocationMBean getLocationMBean() {
        return locationMBean;
    }

    public void setLocationMBean(LocationMBean locationMBean) {
        this.locationMBean = locationMBean;
    }

    public Boolean getNoAccount() {
        return userAccountBean.thereIsNoAccount();
    }

    public boolean isConfirmed() {
        if(StringUtils.isNullOrBlank(userAccount.getConfirmationCode())) {
            return true;
        }
        return false;
    }

    public void validateUserId(FacesContext context, UIComponent toValidate, Object value) {
        String usrId = (String) value;
        if(-1 == usrId.indexOf('@')) {
            throw new ValidatorException(new FacesMessage("Invalid email address."));
        }
    }

    public String getUserAccountByEmail() {
        List<UserAccount> uas = new ArrayList<>(1);
        UserAccount ua = userAccountBean.findByEmail(this.email);
        if (ua != null) {
            uas.add(ua);
        }
        this.userAccounts = uas;
        return "users";
    }

    public List<UserAccount> getUnverifiedUserAccounts() {
        if(unverifiedUserAccounts == null) {
            unverifiedUserAccounts = userAccountBean.findAllUnverifiedAccounts();
        }
        return unverifiedUserAccounts;
    }

    public List<UserAccount> getUnconfirmedUserAccounts() {
        if(unconfirmedUserAccounts == null) {
            unconfirmedUserAccounts = userAccountBean.findAllUnconfirmedAccounts();
        }
        return unconfirmedUserAccounts;
    }

    public List<UserAccount> getDeactivatedUserAccounts() {
        if(deactivatedUserAccounts == null) {
            deactivatedUserAccounts = userAccountBean.findAllDeactivatedUserAccounts();
        }
        return deactivatedUserAccounts;
    }

    @PostConstruct
    public void load() {
        if(!StringUtils.isNullOrBlank(this.id)) {
            this.userAccount = userAccountBean.find(this.id);
            this.authentication = authenticationBean.findByUserAccount(this.userAccount);
            this.historicMessages = messageHistoryBean.findByRecipient(this.userAccount);
            this.userSessions = userSessionBean.findByUserAccount(this.userAccount);
        } else if(this.username != null) {
            this.userAccount = userAccountBean.findByUsername(this.username);
        } else {
            this.userAccount = new UserAccount();
        }

        if(this.userAccount.getCountry() != null) {
            locationMBean.setSelectedCountry(this.userAccount.getCountry().getAcronym());
        } else {
            locationMBean.setSelectedCountry(null);
        }

        if(this.userAccount.getProvince() != null) {
            locationMBean.setSelectedProvince(this.userAccount.getProvince().getId());
        } else {
            locationMBean.setSelectedProvince(null);
        }

        if(this.userAccount.getCity() != null) {
            locationMBean.setSelectedCity(this.userAccount.getCity().getId());
        } else {
            locationMBean.setSelectedCity(null);
        }

        if(this.userAccount.getTimeZone() != null) {
            locationMBean.setSelectedTimeZone(this.userAccount.getTimeZone());
        }

        if(!StringUtils.isNullOrBlank(this.letter)) {
            this.userAccounts = userAccountBean.findAllStartingWith(this.letter);
        }
    }

    public String register() {
        boolean isFirstUser = userAccountBean.thereIsNoAccount();

        if(context.isValidationFailed()) {
            return "registration";
        }

        Authentication authentication = new Authentication();
        final UserAccount newUserAccount;
        try {
            authentication.setUserAccount(this.userAccount);
            authentication.setUsername(userAccount.getUnverifiedEmail());
            authentication.setPassword(this.password);
            newUserAccount = userAccountBean.register(userAccount, authentication);
        } catch(Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            context.addMessage(userId, new FacesMessage(e.getCause().getMessage()));
            return "registration";
        }

        if(!hasMultipleCommunities) {
            Community community = communityBean.findMainCommunity();
            if (community != null) {
                CommunityMember communityMember = new CommunityMember(community, newUserAccount);
                communityMemberBean.save(communityMember);
            }
        }

        if(isFirstUser) {
            context.addMessage(userId, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.getMessage("infoSuccessfulRegistration"), ""));
            return "login";
        } else if(hasMultipleCommunities) {
            return "registration_communities?id=" + userAccount.getId();
        } else {
            context.addMessage(userId, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.getMessage("infoRegistrationConfirmationRequest"), ""));
            return "registration_confirmation";
        }
    }

    public String registerToCommunities() {
        Community community;
        CommunityMember communityMember;
        this.userAccount = userAccountBean.find(this.userAccount.getId());

        for (Map.Entry<String, Boolean> entry : this.selectedCommunities.entrySet()) {
            if(entry.getValue()) {
                community = communityBean.find(entry.getKey());
                communityMember = new CommunityMember(community, this.userAccount);
                communityMemberBean.save(communityMember);
            }
        }

        context.addMessage(userId, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.getMessage("infoRegistrationConfirmationRequest"), ""));
        return "registration_confirmation";
    }

    public String save() {
        UserAccount existingUserAccount = userAccountBean.find(userAccount.getId());

        if(existingUserAccount != null) {
            existingUserAccount.setCountry(this.locationMBean.getCountry());
            existingUserAccount.setProvince(this.locationMBean.getProvince());
            existingUserAccount.setCity(this.locationMBean.getCity());
            existingUserAccount.setFirstName(userAccount.getFirstName());
            existingUserAccount.setLastName(userAccount.getLastName());
            existingUserAccount.setGender(userAccount.getGender());
            existingUserAccount.setWebsite(userAccount.getWebsite());
            existingUserAccount.setTwitter(userAccount.getTwitter());
            userAccountBean.save(existingUserAccount);
        }
        else {
            userAccountBean.save(this.userAccount);
        }

        return "users?faces-redirect=true";
    }

    public String savePersonalData() {
        if(userAccount != null) {
            UserAccount existingUserAccount = userAccountBean.find(userAccount.getId());

            existingUserAccount.setCountry(this.locationMBean.getCountry());
            existingUserAccount.setProvince(this.locationMBean.getProvince());
            existingUserAccount.setCity(this.locationMBean.getCity());
            existingUserAccount.setTimeZone(this.locationMBean.getSelectedTimeZone());
            existingUserAccount.setFirstName(userAccount.getFirstName());
            existingUserAccount.setLastName(userAccount.getLastName());
            existingUserAccount.setGender(userAccount.getGender());
            existingUserAccount.setWebsite(userAccount.getWebsite());
            existingUserAccount.setTwitter(userAccount.getTwitter());
            existingUserAccount.setPublicProfile(userAccount.getPublicProfile());
            userAccountBean.save(existingUserAccount);

            context.getExternalContext().getSessionMap().remove("locationBean");
        }
        return "profile?faces-redirect=true";
    }

    public String savePrivacy() {
        if(userAccount != null) {
            UserAccount existingUserAccount = userAccountBean.find(userAccount.getId());
            existingUserAccount.setPublicProfile(userAccount.getPublicProfile());
            existingUserAccount.setMailingList(userAccount.getMailingList());
            existingUserAccount.setNews(userAccount.getNews());
            existingUserAccount.setGeneralOffer(userAccount.getGeneralOffer());
            existingUserAccount.setJobOffer(userAccount.getJobOffer());
            existingUserAccount.setEvent(userAccount.getEvent());
            existingUserAccount.setSponsor(userAccount.getSponsor());
            existingUserAccount.setSpeaker(userAccount.getSpeaker());

            if(!isPrivacyValid(existingUserAccount)) {
                context.addMessage(null, new FacesMessage("Selecione pelo menos uma das opções de privacidade."));
                return "privacy";
            }

            userAccountBean.save(existingUserAccount);
        }
        return "profile?faces-redirect=true";
    }

    public String confirm() {
        try {
            userAccountBean.confirmUser(userAccount.getConfirmationCode());
        } catch (IllegalArgumentException iae) {
            LOGGER.log(Level.INFO, iae.getMessage(), iae);
            context.addMessage(null, new FacesMessage(iae.getMessage()));
            return "user";
        }
        return "users?faces-redirect=true";
    }

    public String checkUserAsVerified() {
        userAccountBean.markUserAsVerified(this.userAccount);
        return "user?faces-redirect=true";
    }

    public String deactivateMembershipOwnWill() {
        userAccountBean.deactivateMembership(userAccount, DeactivationType.OWNWILL);

        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        try {
            request.logout();
            session.invalidate();
        } catch(ServletException se) {
            LOGGER.log(Level.INFO, se.getMessage(), se);
        }

        return "/index?faces-redirect=true";
    }


    public String deactivateMembership() {
        userAccountBean.deactivateMembership(userAccount, DeactivationType.ADMINISTRATIVE);
        return "users?faces-redirect=true";
    }

    /** Check whether at least one of the privacy options was checked. */
    private boolean isPrivacyValid(UserAccount userAccount) {
        if(userAccount.getPublicProfile() ||
             userAccount.getMailingList() ||
             userAccount.getEvent() ||
             userAccount.getNews() ||
             userAccount.getGeneralOffer() ||
             userAccount.getJobOffer() ||
             userAccount.getSponsor() ||
             userAccount.getSpeaker()) { return true; }
        return false;
    }

    /**
     * Remove the current user permanently and navigate to the users view.
     * @return the next step in the navigation logic.
     */
    public String removeUserAccount() {
        userAccountBean.remove(this.userAccount.getId());
        return "users?faces-redirect=true";
    }

    public List<Community> getExistingCommunities() {
        if(this.existingCommunities == null) {
            this.existingCommunities = communityBean.findAll();
        }
        return this.existingCommunities;
    }

    public Boolean getHasMultipleCommunities() {
        if(hasMultipleCommunities == null) {
            this.hasMultipleCommunities = communityBean.hasMultipleCommunities();
        }
        return this.hasMultipleCommunities;
    }
}