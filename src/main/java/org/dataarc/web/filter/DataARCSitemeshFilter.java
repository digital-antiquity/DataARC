package org.dataarc.web.filter;

import java.io.IOException;
import java.nio.CharBuffer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sitemesh.webapp.SiteMeshFilter;
import org.sitemesh.webapp.contentfilter.ResponseMetaData;

public class DataARCSitemeshFilter extends SiteMeshFilter {

    public DataARCSitemeshFilter() {
        super(null, null, null,false);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    protected boolean postProcess(String contentType, CharBuffer buffer, HttpServletRequest request, HttpServletResponse response, ResponseMetaData metaData)
            throws IOException, ServletException {
        if (response.containsHeader("Content-Length")) {
            // just remove Content-Length
            response.setContentLength(-1);
            // is there method calc bytes before writeValueTo?
        }
        return super.postProcess(contentType, buffer, request, response, metaData);
    }

    
}
