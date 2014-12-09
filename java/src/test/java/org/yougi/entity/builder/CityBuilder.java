package org.yougi.entity.builder;

import org.yougi.entity.City;

public class CityBuilder extends GenericBuilder<City> {

    private CityBuilder() {
        entity = new City();
    }
    
    public static CityBuilder get() {
        return new CityBuilder();
    }
    
}
