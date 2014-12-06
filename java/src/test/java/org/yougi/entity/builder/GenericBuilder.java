package org.yougi.entity.builder;

public abstract class GenericBuilder<Entity> {

    protected Entity entity;
    
    public Entity build() {
        return entity;
    }
    
}
