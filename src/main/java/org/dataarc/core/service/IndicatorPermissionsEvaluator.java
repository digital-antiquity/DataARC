package org.dataarc.core.service;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class IndicatorPermissionsEvaluator implements PermissionEvaluator {

    Logger logger = LoggerFactory.getLogger(getClass());

    
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        logger.debug("Evaluation permission: {} for domain object: {}", permission, targetDomainObject);
        logger.debug("Received authentication: {}", authentication);

        boolean hasPermission = true;

        //https://github.com/pkainulainen/spring-data-solr-examples/blob/master/query-methods/src/main/java/net/petrikainulainen/spring/datasolr/security/authorization/TodoPermissionEvaluator.java
//        if (targetDomainObject.equals("Todo")) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof UserDetails) {
//                logger.debug("User is not anonymous. Evaluation permission");
//                UserDetails userDetails = (UserDetails) principal;
//                String principalRole = getRole(userDetails.getAuthorities());
//                if (principalRole.equals(SecurityRole.ROLE_USER.name())) {
//                    logger.debug("Principal: {} has permission to perform requested operation", userDetails);
//                    hasPermission = true;
//                }
//            }
//            else {
//                logger.debug("User is anonymous. Permission denied.");
//            }
//        }
//        else {
//            logger.debug("Unknown class: {} for target domain Object: {}", targetDomainObject.getClass(), targetDomainObject);
//        }

        logger.debug("Returning: {}", hasPermission);

        return hasPermission;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        logger.debug("Evaluation permission: {} for domain object: {} type: {}", permission, targetId, targetType);
        logger.debug("Received authentication: {}", authentication);

        return false;
    }

}
