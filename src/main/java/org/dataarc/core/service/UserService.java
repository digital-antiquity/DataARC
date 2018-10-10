package org.dataarc.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dataarc.bean.DataArcUser;
import org.dataarc.core.dao.DataArcUserDao;
import org.dataarc.core.dao.EmailDao;
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

/**
 * handles major interactions between users and the database
 * 
 * @author abrin
 *
 */
@Service
public class UserService {

    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String FAMILY_NAME = "family_name";
    private static final String GIVEN_NAME = "given_name";
    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final String EDITOR_ROLE = "ROLE_EDITOR";
    public static final String USER_ROLE = "ROLE_USER";
    public static final String ANONYMOUS_ROLE = "ROLE_ANONYMOUS";

    @Autowired
    DataArcUserDao userDao;

    @Autowired
    EmailDao emailDao;
    
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
            user.setEmail(userEntity.getEmailAddress());
            user.setUsername(userEntity.getName());
            user.setFirstName(userEntity.getFirstName());
            user.setLastName(userEntity.getLastName());
        }
        user.setLastLogin(new Date());
        save(user);

    }

    /**
     * Save or update a user from Oauth2
     * @param user_
     * @param userEntity
     */
    @Transactional(readOnly = false)
    public void saveOrUpdateUser(DataArcUser user_, OAuth2Authentication userEntity) {
        DataArcUser user = user_;
        if (user == null) {
            user = new DataArcUser();
            user.setDateCreated(new Date());
            user.setExternalId(userEntity.getPrincipal().toString());

            LinkedHashMap<String, Object> details = (LinkedHashMap<String, Object>) userEntity.getUserAuthentication().getDetails();
            user.setEmail((String) details.get("email"));
            user.setUsername((String) details.get("name"));
            user.setFirstName((String) details.get(GIVEN_NAME));
            // google
            user.setLastName((String) details.get(FAMILY_NAME));

            // facebook and google have different concepts of users 
            if (details.get(LAST_NAME) != null && StringUtils.isNotBlank((String) details.get(LAST_NAME))) {
                user.setLastName((String) details.get(LAST_NAME));
            }
            if (details.get(FIRST_NAME) != null && StringUtils.isNotBlank((String) details.get(FIRST_NAME))) {
                user.setFirstName((String) details.get(FIRST_NAME));
            }

        }
        user.setLastLogin(new Date());
        save(user);

    }

    /**
     * Figure out what to do with a user when we get passed it from the controller... because of Oauth2, we may have work to do, like creating the user
     * 
     * @param authentication
     * @return
     */
    @Transactional(readOnly = false)
    public DataArcUser reconcileUser(Authentication authentication) {
        logger.trace("{}", authentication);
        String username = authentication.getName();
        String userId = null;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Object details = authentication.getDetails();
        logger.trace("   username: {}", username);
        logger.trace("authorities: {}", authorities);
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication userEntity = (OAuth2Authentication) authentication;
            userId = userEntity.getName();
            logger.trace("    details: {} | {}", details, details.getClass());
            logger.trace("    principal: {} | {}", authentication.getPrincipal(), authentication.getPrincipal().getClass());
            // logger.debug("{}", userEntity.getUserAuthentication());
            // logger.debug("{}", userEntity.getCredentials());
            logger.trace("     userId: {}", userId);
            DataArcUser user = findByExternalId(userId);
            // authentication.set
            boolean isNew = false;
            if (user == null) {
                isNew = true;
            }
            saveOrUpdateUser(user, userEntity);
            if (isNew) {
                emailDao.sendWelcomeEmail(user, EDITOR_ROLE);
            }
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

    @Transactional(readOnly = true)
    public void enhanceGroupMembership(DataArcUser user_, Collection<GrantedAuthority> collection) {
        collection.add(new SimpleGrantedAuthority(ANONYMOUS_ROLE));
        collection.add(new SimpleGrantedAuthority(USER_ROLE));
        if (user_.isAdmin()) {
            collection.add(new SimpleGrantedAuthority(UserService.EDITOR_ROLE));
            collection.add(new SimpleGrantedAuthority(UserService.ADMIN_ROLE));
        }
        if (user_.isEditor()) {
            collection.add(new SimpleGrantedAuthority(UserService.EDITOR_ROLE));
        }

    }

    @Transactional(readOnly = false)
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
        user.setFirstName((String) map.get(GIVEN_NAME));
        user.setLastName((String) map.get(FAMILY_NAME));
        user.setLastLogin(new Date());
        save(user);

        return user;
    }

    @Transactional(readOnly = true)
    public List<DataArcUser> findAll() {
        return userDao.findAll();
    }

    @Transactional(readOnly = false)
    public void makeAdmin(Long userId) {
        DataArcUser findByUserId = userDao.findById(userId);
        findByUserId.setAdmin(true);
        save(findByUserId);
    }

    @Transactional(readOnly = false)
    public void makeEditor(Long userId) {
        DataArcUser findByUserId = userDao.findById(userId);
        findByUserId.setEditor(true);
        save(findByUserId);
    }

    @Transactional(readOnly = false)
    public void deleteById(Long id) {
        DataArcUser findByUserId = userDao.findById(id);
        userDao.delete(findByUserId);

    }

    @Transactional(readOnly = false)
    public void updateRole(Long id, String role) {
        DataArcUser user = userDao.findById(id);
        if (StringUtils.containsIgnoreCase(ADMIN_ROLE, role)) {
            user.setAdmin(true);
            user.setEditor(false);
        } else if (StringUtils.containsIgnoreCase(EDITOR_ROLE, role)) {
            user.setEditor(true);
            user.setAdmin(false);
            emailDao.sendEmail(user, EDITOR_ROLE);
        } else {
            user.setAdmin(false);
            user.setEditor(false);
        }
        save(user);

    }

}
