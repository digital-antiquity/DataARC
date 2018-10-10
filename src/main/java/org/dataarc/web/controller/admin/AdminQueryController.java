package org.dataarc.web.controller.admin;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.dataarc.core.service.CombinatorService;
import org.dataarc.core.service.UserService;
import org.dataarc.web.AbstractController;
import org.dataarc.web.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.client.MongoCursor;

@Controller
@Secured(UserService.ADMIN_ROLE)
public class AdminQueryController extends AbstractController {

    @Autowired
    CombinatorService indicatorService;

    @RequestMapping(UrlConstants.ADMIN_QUERY)
    public String index() {
        return "admin/query";
    }
    
    @RequestMapping(path=UrlConstants.ADMIN_QUERY_RESULT, method = RequestMethod.POST)
    public ModelAndView search(@RequestParam(name="query", required=true) String json) throws Exception {
        ModelAndView mav = new ModelAndView("admin/queryResult");
        MongoCursor<Document> iterator = indicatorService.search(json).iterator();
        List<Document> list = new ArrayList<Document>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        mav.addObject("results",list);
        
        return mav;
    }
    

}
