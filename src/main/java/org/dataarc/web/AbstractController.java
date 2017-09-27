package org.dataarc.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.dataarc.bean.DataArcUser;
import org.dataarc.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class AbstractController {

    private static final String CONTENT_LENGTH = "Content-Length";

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private Environment env;

    @Autowired
    UserService userService;

    @ModelAttribute("contextPath")
    public String getVersion() {
        return servletContext.getContextPath();
    }

    @ModelAttribute("rollbarKey")
    public String getRollbarKey() {
        return env.getProperty("rollbar.key");
    }

    public String getCurrentUserId() {
        DataArcUser entity = getUser();
        if (entity == null) {
            return null;
        }
        return entity.getExternalId();
    }

    @ModelAttribute("authenticated")
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return true;
    }

    @ModelAttribute("admin")
    public boolean isAdmin() {
        DataArcUser user = getUser();
        if (user == null) {
            return false;
        }
        return user.isAdmin();
    }

    @ModelAttribute("editor")
    public boolean isEditor() {
        DataArcUser user = getUser();
        if (user == null) {
            return false;
        }
        if (user.isAdmin()) {
            return true;
        }
        return user.isEditor();
    }

    @ModelAttribute("currentUser")
    public DataArcUser getUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        DataArcUser value = userService.reconcileUser(authentication);
        return value;
    }

    @ModelAttribute("currentUserDisplayName")
    public String getCurrentUserDisplayName() {
        DataArcUser entity = getUser();
        if (entity == null) {
            return "none";
        }
        return entity.getDisplayName();
    }

    @ModelAttribute
    public void setVaryResponseHeader(HttpServletResponse response) {
        response.setHeader(CONTENT_LENGTH, "-1");
    }

}
