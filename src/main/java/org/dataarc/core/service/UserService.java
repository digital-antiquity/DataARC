package org.dataarc.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dataarc.bean.DataArcUser;
import org.dataarc.core.dao.DataArcUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atlassian.crowd.integration.rest.entity.UserEntity;

@Service
public class UserService {

    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final String EDITOR_ROLE = "ROLE_EDITOR";
    public static final String USER_ROLE = "ROLE_USER";

    @Autowired
    DataArcUserDao userDao;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional(readOnly = true)
    public DataArcUser findByExternalId(String userId) {
        return userDao.findByUserId(userId);
    }

    @Transactional(readOnly = false)
    public void save(DataArcUser user) {
        userDao.save(user);
    }

    @Transactional(readOnly = false)
    public void saveOrUpdateUser(DataArcUser user_, UserEntity userEntity) {
        DataArcUser user = user_;
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
        save(user);

    }

    @Transactional(readOnly = false)
    public void saveOrUpdateUser(DataArcUser user_, OAuth2Authentication userEntity) {
        DataArcUser user = user_;
        if (user == null) {
            user = new DataArcUser();
            user.setDateCreated(new Date());
            user.setExternalId(userEntity.getPrincipal().toString());
        }

        LinkedHashMap<String, Object> details = (LinkedHashMap<String, Object>) userEntity.getUserAuthentication().getDetails();
        logger.debug("details:{}", details);
        user.setEmail((String) details.get("email"));
        user.setUsername((String) details.get("name"));
        user.setFirstName((String) details.get("given_name"));
        //google
        user.setLastName((String) details.get("family_name"));
        // facebook
        if (details.get("last_name") != null && StringUtils.isNotBlank((String)details.get("last_name"))) {
            user.setLastName((String)details.get("last_name"));
        }
        user.setLastLogin(new Date());
        save(user);

    }

    @Transactional(readOnly = false)
    public DataArcUser reconcileUser(Authentication authentication) {
        logger.trace("{}", authentication);
        String username = authentication.getName();
        String userId = null;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Object details = authentication.getDetails();
        logger.trace("   username: {}", username);
        logger.trace("authorities: {}", authorities);
        logger.debug("    details: {}", details);
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication userEntity = (OAuth2Authentication) authentication;
            userId = userEntity.getName();
            logger.trace("    details: {} | {}", details, details.getClass());
            logger.trace("    principal: {} | {}", authentication.getPrincipal(), authentication.getPrincipal().getClass());
            logger.debug("{}", userEntity.getUserAuthentication());
            logger.debug("{}", userEntity.getCredentials());
            logger.trace("     userId: {}", userId);
            DataArcUser user = findByExternalId(userId);
//            authentication.set
            saveOrUpdateUser(user, userEntity);
            return user;
        }

        if (details instanceof UserEntity) {
            UserEntity userEntity = (UserEntity) details;
            userId = userEntity.getExternalId();
            logger.trace("     userId: {}", userId);
            DataArcUser user = findByExternalId(userId);
            saveOrUpdateUser(user, userEntity);
            return user;
        }
        return null;
    }

    @Transactional(readOnly=true)
    public void enhanceGroupMembership(DataArcUser user_, Collection<GrantedAuthority> collection) {
        collection.add(new SimpleGrantedAuthority(USER_ROLE));
        if (user_.isAdmin()) {
            collection.add(new SimpleGrantedAuthority(UserService.EDITOR_ROLE));
            collection.add(new SimpleGrantedAuthority(UserService.ADMIN_ROLE));
        }
        if (user_.isEditor()) {
            collection.add(new SimpleGrantedAuthority(UserService.EDITOR_ROLE));
        }
        
    }

    @Transactional(readOnly=false)
    public DataArcUser findSaveUpdateUser(Map<String, Object> map) {
            String id = (String) map.get("id");
            DataArcUser user = findByExternalId(id);
            if (user == null) {
                user = new DataArcUser();
                user.setDateCreated(new Date());
                user.setExternalId(id);
            }

            user.setEmail((String) map.get("email"));
            user.setUsername((String) map.get("name"));
            user.setFirstName((String) map.get("given_name"));
            user.setLastName((String) map.get("family_name"));
            user.setLastLogin(new Date());
            save(user);
            
            return user;
    }

    @Transactional(readOnly=true)
    public List<DataArcUser> findAll() {
        return userDao.findAll();
    }

    @Transactional(readOnly=false)
    public void makeAdmin(String userId) {
        DataArcUser findByUserId = userDao.findByUserId(userId);
        findByUserId.setAdmin(true);
        save(findByUserId);
    }

    
    @Transactional(readOnly=false)
    public void makeEditor(String userId) {
        DataArcUser findByUserId = userDao.findByUserId(userId);
        findByUserId.setEditor(true);
        save(findByUserId);
    }

    @Transactional(readOnly=false)
    public void deleteById(String id) {
        DataArcUser findByUserId = userDao.findByUserId(id);
        userDao.delete(findByUserId);
        
    }

}
