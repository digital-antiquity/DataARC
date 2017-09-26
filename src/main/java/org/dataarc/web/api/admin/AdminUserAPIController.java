package org.dataarc.web.api.admin;

import java.util.List;

import org.dataarc.bean.DataArcUser;
import org.dataarc.core.service.UserService;
import org.dataarc.web.UrlConstants;
import org.dataarc.web.api.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Secured(UserService.ADMIN_ROLE)
public class AdminUserAPIController extends AbstractRestController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = UrlConstants.ADMIN_MAKE_EDITOR, method = RequestMethod.POST)
    public boolean makeEditor(@RequestParam(value = "id", required = true) Long userId) throws Exception {
        try {
            userService.makeEditor(userId);
        } catch (Exception e) {
            logger.error("{}", e, e);
            return false;
        }
        return true;
    }

    @RequestMapping(path = UrlConstants.ADMIN_MAKE_ADMIN, method = RequestMethod.POST)
    public boolean makeAdmin(@RequestParam(value = "id", required = true) Long userId) throws Exception {
        try {
            userService.makeAdmin(userId);
        } catch (Exception e) {
            logger.error("{}", e, e);
            return false;
        }
        return true;
    }

    @RequestMapping(path = UrlConstants.ADMIN_LIST_USERS, method = RequestMethod.GET)
    public List<DataArcUser> listUsers() throws Exception {
        return userService.findAll();
    }

}
