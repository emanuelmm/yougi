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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DualListModel;
import org.yougi.annotation.ManagedProperty;
import org.yougi.business.AccessGroupBean;
import org.yougi.business.UserGroupBean;
import org.yougi.entity.AccessGroup;
import org.yougi.entity.Address;
import org.yougi.entity.City;
import org.yougi.entity.Country;
import org.yougi.entity.Province;
import org.yougi.entity.UserAccount;
import org.yougi.partnership.business.PartnerBean;
import org.yougi.partnership.business.RepresentativeBean;
import org.yougi.partnership.entity.Partner;
import org.yougi.partnership.entity.Representative;
import org.yougi.web.model.LocationMBean;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
public class PartnerMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private PartnerBean partnerBean;

    @EJB
    private AccessGroupBean accessGroupBean;

    @EJB
    private UserGroupBean userGroupBean;

    @EJB
    private RepresentativeBean representativeBean;

    @Inject
    @ManagedProperty("#{param.id}")
    private String id;

    @Inject
    private LocationMBean locationMBean;

    private Partner partner;
    private List<Partner> partners;
    private List<Representative> representatives;

    // List of users from the group of partners, which are candidates for
    // representative.
    private DualListModel<UserAccount> candidates;

    public PartnerMBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public List<Partner> getPartners() {
        if (partners == null) {
            this.partners = partnerBean.findPartners();
        }
        return partners;
    }

    public List<Representative> getRepresentatives() {
        return representatives;
    }

    public LocationMBean getLocationMBean() {
        return locationMBean;
    }

    public void setLocationMBean(LocationMBean locationMBean) {
        this.locationMBean = locationMBean;
    }

    public DualListModel<UserAccount> getCandidates() {
        return candidates;
    }

    public void setCandidates(DualListModel<UserAccount> candidates) {
        this.candidates = candidates;
    }

    @PostConstruct
    public void load() {
        List<UserAccount> reps = new ArrayList<>();
        AccessGroup accessGroup = accessGroupBean.findAccessGroupByName("partners");
        List<UserAccount> usersGroup = userGroupBean.findUsersGroup(accessGroup);

        if (this.id != null && !this.id.isEmpty()) {
            this.partner = partnerBean.find(id);

            locationMBean.initialize();

            if (this.partner.getAddress().getCountry() != null) {
                locationMBean.setSelectedCountry(this.partner.getAddress().getCountry().getAcronym());
            }

            if (this.partner.getAddress().getProvince() != null) {
                locationMBean.setSelectedProvince(this.partner.getAddress().getProvince().getId());
            }

            if (this.partner.getAddress().getCity() != null) {
                locationMBean.setSelectedCity(this.partner.getAddress().getCity().getId());
            }

            reps.addAll(representativeBean.findRepresentativePersons(this.partner));
            usersGroup.removeAll(reps);
        } else {
            this.partner = new Partner().setAddress(new Address());
        }
        this.candidates = new DualListModel<>(usersGroup, reps);
    }

    public String save() {
        Country country = this.locationMBean.getCountry();
        if (country != null) {
            this.partner.getAddress().setCountry(country);
        }

        Province province = this.locationMBean.getProvince();
        if (province != null) {
            this.partner.getAddress().setProvince(province);
        }

        City city = this.locationMBean.getCity();
        if (city != null) {
            this.partner.getAddress().setCity(city);
        }

        List<UserAccount> reps = new ArrayList<>();
        List<UserAccount> selectedCandidates = this.candidates.getTarget();
        for (UserAccount selectedCandidate : selectedCandidates) {
            reps.add(new UserAccount(selectedCandidate.getId()));
        }

        representativeBean.save(this.partner, reps);

        return "partners?faces-redirect=true";
    }

    public String remove() {
        partnerBean.remove(this.partner.getId());
        return "partners?faces-redirect=true";
    }
}