package org.dataarc.web.controller.mapping;

import org.dataarc.web.AbstractController;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MappingController extends AbstractController {

    @RequestMapping("/mapping/")
    public String mapping() {
        logger.debug("{}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.debug("{}", userDetails);
        return "mapping/mapping";
    }

}
