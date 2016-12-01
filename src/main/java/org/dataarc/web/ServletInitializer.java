package org.dataarc.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.dataarc.config.DataArcConfiguration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ServletInitializer implements WebApplicationInitializer  {

    
    @Override
    public void onStartup(ServletContext container) {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(DataArcWebConfig.class);
        ctx.setServletContext(container);


        ServletRegistration.Dynamic servlet = container.addServlet("dispatcher", new DispatcherServlet(ctx));

        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
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
