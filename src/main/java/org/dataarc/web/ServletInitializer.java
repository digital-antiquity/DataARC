package org.dataarc.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.opensymphony.sitemesh.webapp.SiteMeshFilter;

public class ServletInitializer implements WebApplicationInitializer  {

    
    @Override
    public void onStartup(ServletContext container) {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(DataArcWebConfig.class);
        ctx.setServletContext(container);


//        addSitemeshFilterToServletContext(container);
        ServletRegistration.Dynamic servlet = container.addServlet("dispatcher", new DispatcherServlet(ctx));

        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
    }
    
    
    private void addSitemeshFilterToServletContext(ServletContext servletContext) {
        FilterRegistration.Dynamic sitemesh = servletContext.addFilter("sitemesh", new SiteMeshFilter());
        EnumSet<DispatcherType> sitemeshDispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        sitemesh.addMappingForUrlPatterns(sitemeshDispatcherTypes, true, "/*");
    }
     
//    @Override
//    protected Class<?>[] getServletConfigClasses() {
//        // GolfingWebConfig defines beans that would be in golfing-servlet.xml
//        return new Class[] { DataArcWebConfig.class };
//    }
//
//    @Override
//    protected Class<?>[] getRootConfigClasses() {
//        return new Class[] {DataArcConfiguration.class };
//    }
//
//    @Override
//    protected String[] getServletMappings() {
//        return new String[] {"/*"};
//    }
}
