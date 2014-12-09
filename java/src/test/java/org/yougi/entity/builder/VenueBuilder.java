package org.yougi.entity.builder;

import org.yougi.event.entity.Venue;

public class VenueBuilder extends GenericBuilder<Venue> {

    private VenueBuilder() {
        entity = new Venue();
    }
    
    public static VenueBuilder get() {
        return new VenueBuilder();
    }
    
}
