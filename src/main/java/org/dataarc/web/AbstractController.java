package org.dataarc.web;

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
        if (details instanceof UserEntity) {
            UserEntity userEntity = (UserEntity) details;
            userId = userEntity.getExternalId();
            logger.trace("     userId: {}", userId);
            DataArcUser user = userService.findByExternalId(userId);
            if (user == null) {
                user = new DataArcUser();
                user.setDateCreated(new Date());
                user.setExternalId(userEntity.getExternalId());
            }
            user.setEmail(userEntity.getEmailAddress());
            user.setUsername(userEntity.getName());
            user.setFirstName(userEntity.getFirstName());
            user.setLastName(userEntity.getLastName());
            user.setLastLogin(new Date());
            userService.save(user);
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
