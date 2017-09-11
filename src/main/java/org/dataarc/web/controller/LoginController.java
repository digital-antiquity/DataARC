package org.dataarc.web.controller;

import org.dataarc.web.AbstractController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController extends AbstractController {

    @RequestMapping("/login")
    public String login() {
        if (isAuthenticated()) {
            return "redirect:/a/home";
        }
        return "login";
    }
    
   


    @RequestMapping("/logout")
    public String logout() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        logger.debug("logging {} out", authentication);
        securityContext.setAuthentication(null);
        
        return "login";
    }

}
