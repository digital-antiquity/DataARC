package org.dataarc.web.controller.temporal;

import org.dataarc.core.service.TemporalCoverageService;
import org.dataarc.core.service.UserService;
import org.dataarc.util.View;
import org.dataarc.web.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonView;

@Controller
@Secured(UserService.EDITOR_ROLE)
public class TemporalController {


    @Autowired
    private TemporalCoverageService temporalCoverageService;


    @RequestMapping(path = UrlConstants.LIST_COVERAGE)
    @JsonView(View.Schema.class)
    public ModelAndView coverage() {
        ModelAndView mav = new ModelAndView("coverage/list");
        mav.addObject("coverage", temporalCoverageService.findAll());
        return mav;
    }

}
