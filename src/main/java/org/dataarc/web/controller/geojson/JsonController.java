package org.dataarc.web.controller.geojson;

import org.dataarc.core.service.JsonFileService;
import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JsonController extends AbstractController {
    @Autowired
    JsonFileService jsonService;
    
    @Secured(UserService.ADMIN_ROLE)
    @RequestMapping(path=UrlConstants.LIST_JSON)
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("geojson/list");
        mav.addObject("files", jsonService.findAll());
        return mav;
    }


    @RequestMapping(path = UrlConstants.DELETE_GEOJSON, method = RequestMethod.POST)
    public ModelAndView deleteSchema(@PathVariable(value = "id", required = true) Long id) throws Exception {
        ModelAndView mav = new ModelAndView("geojson/list");
        jsonService.deleteById(id);
        return mav;
    }

}
