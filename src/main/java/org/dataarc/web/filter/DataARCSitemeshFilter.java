package org.dataarc.web.filter;

import java.io.IOException;
import java.nio.CharBuffer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sitemesh.DecoratorSelector;
import org.sitemesh.content.ContentProcessor;
import org.sitemesh.webapp.SiteMeshFilter;
import org.sitemesh.webapp.WebAppContext;
import org.sitemesh.webapp.contentfilter.ResponseMetaData;
import org.sitemesh.webapp.contentfilter.Selector;

public class DataARCSitemeshFilter extends SiteMeshFilter {

    public DataARCSitemeshFilter(Selector selector, ContentProcessor contentProcessor, DecoratorSelector<WebAppContext> decoratorSelector,
            boolean includeErrorPages) {
        super(selector, contentProcessor, decoratorSelector, includeErrorPages);
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
