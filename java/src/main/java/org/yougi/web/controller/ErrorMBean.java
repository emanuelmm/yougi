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
package org.yougi.web.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
public class ErrorMBean {

    private final ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

    private Throwable throwable;

    public Integer getStatusCode() {
        return (Integer) externalContext.getRequestMap().get("javax.servlet.error.status_code");
    }

    public String getMessage() {
        return String.valueOf(externalContext.getRequestMap().get("javax.servlet.error.message"));
    }

    public String getExceptionType() {
        return String.valueOf(externalContext.getRequestMap().get("javax.servlet.error.exception_type"));
    }

    public String getException() {
        if(throwable != null) {
            return throwable.toString();
        }
        else {
            return null;
        }
    }

    public String getStackTrace() {
        return getStackTrace(this.throwable, "");
    }

    private String getStackTrace(Throwable throwable, String stackTrace) {
        if(throwable == null) {
            return "-";
        }

        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        StringBuilder strStackTrace = new StringBuilder(stackTrace);
        for(StackTraceElement stackTraceElement: stackTraceElements) {
            strStackTrace.append(stackTraceElement.getClassName());
            strStackTrace.append(":");
            strStackTrace.append(stackTraceElement.getMethodName());
            strStackTrace.append(":");
            strStackTrace.append(stackTraceElement.getLineNumber());
            strStackTrace.append("\n");
        }

        if(throwable.getCause() != null) {
            strStackTrace.append("Caused by: \n");
            strStackTrace.append(getStackTrace(throwable.getCause(), strStackTrace.toString()));
        }

        return strStackTrace.toString();
    }

    public String getRequestURI() {
        return (String) externalContext.getRequestMap().get("javax.servlet.error.request_uri");
    }

    public String getServletName() {
        return (String) externalContext.getRequestMap().get("javax.servlet.error.servlet_name");
    }

    @PostConstruct
    public void load() {
        this.throwable = (Throwable) externalContext.getRequestMap().get("javax.servlet.error.exception");
    }
}