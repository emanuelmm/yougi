package org.cejug.yougi.util;


import org.cejug.yougi.qualifier.UserName;

import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class ProducesUtil {

    @Produces
    @UserName
    public String getRemoteUser() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.getRemoteUser();
    }
}
