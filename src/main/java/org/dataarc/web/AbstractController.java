package org.dataarc.web;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.dataarc.bean.DataArcUser;
import org.dataarc.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.atlassian.crowd.integration.rest.entity.UserEntity;

public abstract class AbstractController {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private ServletContext servletContext;
    
    @Autowired
    UserService userService;
    
    @ModelAttribute("contextPath")
    public String getVersion() {
       return servletContext.getContextPath();
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

    
    @ModelAttribute("currentUser")
    public DataArcUser getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        logger.debug("{}", authentication);
        String username = authentication.getName();
        String userId = null;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Object details = authentication.getDetails();
        logger.trace("   username: {}", username);
        logger.trace("authorities: {}", authorities);
        logger.trace("    details: {}", details);
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication userEntity = (OAuth2Authentication) authentication;
            userId = userEntity.getName();
            logger.debug("    details: {} | {}", details, details.getClass());
            logger.debug("    principal: {} | {}", authentication.getPrincipal(), authentication.getPrincipal().getClass());
            logger.debug("{}", userEntity.getUserAuthentication());
            logger.debug("{}", userEntity.getCredentials());
            logger.trace("     userId: {}", userId);
            DataArcUser user = userService.findByExternalId(userId);
            userService.saveOrUpdateUser(user, userEntity);
            return user;
        }

        if (details instanceof UserEntity) {
            UserEntity userEntity = (UserEntity) details;
            userId = userEntity.getExternalId();
            logger.trace("     userId: {}", userId);
            DataArcUser user = userService.findByExternalId(userId);
            userService.saveOrUpdateUser(user, userEntity);
            return user;
        }
        return null;
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
        response.setHeader("Content-Length", "-1");
    }

}
