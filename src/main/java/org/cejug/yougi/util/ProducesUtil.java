package org.cejug.yougi.util;


import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.qualifier.UserName;

import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

public class ProducesUtil {

    @Inject
    private UserAccountBean userAccountBean;

    @Produces @Named @UserName
    public String getUserName() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.getRemoteUser();
    }

    @Produces @Named
    public String getFirstName() {
        String username = getUserName();
        UserAccount userAccount = userAccountBean.findByUsername(username);
        return userAccount == null ? "" : userAccount.getFirstName();
    }
}
