package org.yougi.entity.builder;

import org.yougi.entity.Province;

public class ProvinceBuilder extends GenericBuilder<Province> {

    private ProvinceBuilder() {
        entity = new Province();
    }
    
    public static ProvinceBuilder get() {
        return new ProvinceBuilder();
    }
}
