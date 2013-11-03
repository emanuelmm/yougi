/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cejug.yougi.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.cejug.yougi.knowledge.entity.WebSource;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@FacesConverter(value="WebSourceConverter")
public class WebSourceConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(value == null || value.isEmpty()) {
            return null;
        }
        return new WebSource(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (!(value instanceof WebSource)) {
            return null;
        }
        WebSource webSource = (WebSource) value;
        return webSource.getId();
    }
}