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

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Embeddable
@Access(AccessType.FIELD)
public class Address {

    private String address;

    @ManyToOne
    @JoinColumn(name = "city")
    private City city;

    @ManyToOne
    @JoinColumn(name = "province")
    private Province province;

    @ManyToOne
    @JoinColumn(name = "country")
    private Country country;

    @Column(name = "postal_code")
    private String postalCode;

    public String getAddress() {
        return address;
    }

    public Address setAddress(String address) {
        this.address = address;
        return this;
    }

    public City getCity() {
        return city;
    }

    public Address setCity(City city) {
        this.city = city;
        return this;
    }

    public Province getProvince() {
        return province;
    }

    public Address setProvince(Province province) {
        this.province = province;
        return this;
    }

    public Country getCountry() {
        return country;
    }

    public Address setCountry(Country country) {
        this.country = country;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        StringBuilder fullAddress = new StringBuilder();
        String commaSeparator = ", ";
        if (address != null && !address.isEmpty()) {
            fullAddress.append(address);
        }

        if (city != null) {
            if (!fullAddress.toString().isEmpty()) {
                fullAddress.append(commaSeparator);
            }

            fullAddress.append(city.getName());
        }

        if (province != null) {
            if (!fullAddress.toString().isEmpty()) {
                fullAddress.append(commaSeparator);
            }

            fullAddress.append(province.getName());
        }

        if (country != null) {
            if (!fullAddress.toString().isEmpty()) {
                fullAddress.append(" - ");
            }

            fullAddress.append(country.getName());
        }

        if (postalCode != null) {
            if (!fullAddress.toString().isEmpty()) {
                fullAddress.append(".");
            }
            fullAddress.append(" ");
            if (country != null) {
                fullAddress.append(": ");
                fullAddress.append(country.getName());
            }
        }

        return fullAddress.toString();
    }
}