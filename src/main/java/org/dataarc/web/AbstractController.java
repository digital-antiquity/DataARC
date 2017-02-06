package org.dataarc.web;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class AbstractController {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private ServletContext servletContext;
    
    @ModelAttribute("contextPath")
    public String getVersion() {
       return servletContext.getContextPath();
    }
    
    public Object getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
