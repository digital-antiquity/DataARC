package org.dataarc.web.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


public class ServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer  {

    public static final String ALL_PATHS = "/*";
    EnumSet<DispatcherType> allDispacherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR);
    private WebApplicationContext context;

    @Override
    protected void registerContextLoaderListener(ServletContext container) {
        super.registerContextLoaderListener(container);
        container.addListener(RequestContextListener.class);
//        addSitemeshFilterToServletContext(container);
    }
    
    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] {new OpenEntityManagerInViewFilter(), new ConfigurableSiteMeshFilter()};
    }

//    private void addSitemeshFilterToServletContext(ServletContext servletContext) {
//        FilterRegistration.Dynamic sitemesh = servletContext.addFilter("sitemesh", new ConfigurableSiteMeshFilter());
//        EnumSet<DispatcherType> sitemeshDispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
//        sitemesh.addMappingForUrlPatterns(sitemeshDispatcherTypes, true, "/*");
//    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { DataArcWebConfig.class , SecurityConfig.class };// 
    }


    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {

//        File uploadDirectory = ServiceConfiguration.CRM_STORAGE_UPLOADS_DIRECTORY;
//
//        MultipartConfigElement multipartConfigElement = 
//            new MultipartConfigElement(uploadDirectory.getAbsolutePath(),
//                maxUploadSizeInMb, maxUploadSizeInMb * 2, maxUploadSizeInMb / 2);
//
//        registration.setMultipartConfig(multipartConfigElement);

    }
    
    @Override
    protected WebApplicationContext createRootApplicationContext() {
        context = (WebApplicationContext) super.createRootApplicationContext();
        ((ConfigurableEnvironment) context.getEnvironment()).setActiveProfiles("mongo");
        return context;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }

}
