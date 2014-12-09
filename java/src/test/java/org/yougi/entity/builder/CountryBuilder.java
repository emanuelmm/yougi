package org.yougi.entity.builder;

import org.yougi.entity.Country;

public class CountryBuilder extends GenericBuilder<Country> {

    private CountryBuilder() {
        entity = new Country();
    }
    
    public static CountryBuilder get() {
        return new CountryBuilder();
    }
    
}
