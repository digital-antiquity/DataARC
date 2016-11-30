package org.dataarc.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.dataarc.config.DataArcConfiguration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ServletConfig extends AbstractAnnotationConfigDispatcherServletInitializer implements WebApplicationInitializer  {

    
    @Override
    public void onStartup(ServletContext container) {
        ServletRegistration.Dynamic registration = container.addServlet("data-arc", new DispatcherServlet());
        registration.setLoadOnStartup(1);
        registration.addMapping("/*");
    }
    
    @Override
    protected Class<?>[] getServletConfigClasses() {
        // GolfingWebConfig defines beans that would be in golfing-servlet.xml
        return new Class[] { DataArcWebConfig.class };
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] {DataArcConfiguration.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/*"};
    }
}
