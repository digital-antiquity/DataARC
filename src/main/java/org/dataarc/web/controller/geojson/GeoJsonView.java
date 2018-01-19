package org.dataarc.web.controller.geojson;

import org.dataarc.core.service.JsonFileService;
import org.dataarc.web.UrlConstants;
import org.dataarc.web.api.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeoJsonView extends AbstractRestController {

    @Autowired
    private JsonFileService jsonFileService;

    @RequestMapping(path = UrlConstants.GEOJSON_VIEW, produces = { "application/json; charset=UTF-8" })
    public String getFile(@PathVariable(name = "id") Long id) throws Exception {
        return jsonFileService.findById(id);
    }

}
